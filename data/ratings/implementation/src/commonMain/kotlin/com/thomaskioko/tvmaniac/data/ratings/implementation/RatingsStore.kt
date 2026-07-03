package com.thomaskioko.tvmaniac.data.ratings.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.toDbProvider
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrNull
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.api.ProviderMetaDao
import com.thomaskioko.tvmaniac.data.ratings.api.ProviderRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsDao
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.RATINGS_SYNC
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
@SingleIn(AppScope::class)
public class RatingsStore(
    private val activeSource: () -> RatingsRemoteDataSource?,
    private val providerMetaDao: ProviderMetaDao,
    private val ratingsDao: RatingsDao,
    private val database: TvManiacDatabase,
    private val requestManagerRepository: RequestManagerRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, ProviderRating> by storeBuilder(
    fetcher = Fetcher.of { showId: Long ->
        val source = activeSource()
            ?: throw AuthenticationException("No active sync provider")
        val provider = source.provider
        val providerShowId = resolveProviderShowId(showId, provider, database)
            ?: throw AuthenticationException("No $provider show id for show $showId")

        FetchedProviderRating(
            provider = provider.toDbProvider(),
            communityRating = source.getShowCommunityRating(providerShowId).getOrThrow(),
            userRating = source.getShowUserRating(providerShowId).getOrNull(),
        )
    },
    sourceOfTruth = SourceOfTruth.of<Long, FetchedProviderRating, ProviderRating>(
        reader = { showId: Long ->
            val provider = activeSource()?.provider?.toDbProvider()
            if (provider != null) providerMetaDao.observeProviderRating(showId, provider) else flowOf(null)
        },
        writer = { showId: Long, fetched: FetchedProviderRating ->
            providerMetaDao.upsertProviderRating(
                showId = showId,
                provider = fetched.provider,
                rating = fetched.communityRating.rating,
                voteCount = fetched.communityRating.votes,
                lastSyncedAt = dateTimeProvider.nowMillis(),
            )

            fetched.userRating?.let { userRating ->
                ratingsDao.saveRemoteShowRating(
                    showId = showId,
                    userRating = userRating.toLong(),
                    ratedAt = dateTimeProvider.nowMillis(),
                )
            }

            requestManagerRepository.upsert(
                entityId = showId,
                requestType = RATINGS_SYNC.name,
            )
        },
        delete = { _: Long -> },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { rating: ProviderRating ->
        withContext(dispatchers.io) {
            !requestManagerRepository.isRequestExpired(
                entityId = rating.showId,
                requestType = RATINGS_SYNC.name,
                threshold = RATINGS_SYNC.duration,
            )
        }
    },
).build()

private data class FetchedProviderRating(
    val provider: Provider,
    val communityRating: CommunityRating,
    val userRating: Int?,
)

private fun resolveProviderShowId(
    showId: Long,
    provider: AccountProvider,
    database: TvManiacDatabase,
): Long? = database.tvshowExternalIdQueries
    .externalIdForShow(showId = Id(showId), provider = provider.toDbProvider())
    .executeAsOneOrNull()
    ?.toLongOrNull()
