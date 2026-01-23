package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCHLIST_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType.SHOWS_WATCHLISTED
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class WatchlistStore(
    private val traktListDataSource: TraktListRemoteDataSource,
    private val followedShowsDao: FollowedShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityDao: TraktActivityDao,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<FollowedShowEntry>> by storeBuilder(
    fetcher = apiFetcher { _: Unit ->
        traktListDataSource.getWatchList(sortBy = "rank")
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> followedShowsDao.entriesObservable() },
        writer = { _: Unit, response: List<TraktFollowedShowResponse> ->
            val networkEntries = response.map { it.toFollowedShowEntry() }

            transactionRunner {
                val currentEntries = followedShowsDao.entriesWithNoPendingAction()
                val currentByTraktId = currentEntries.associateBy { it.traktId }
                val networkTraktIds = networkEntries.map { it.traktId }.toSet()

                networkEntries.forEach { networkEntry ->
                    val existingEntry = currentByTraktId[networkEntry.traktId]
                    val _ = followedShowsDao.upsert(networkEntry.copy(id = existingEntry?.id ?: 0))
                }

                currentEntries.forEach { localEntry ->
                    if (localEntry.traktId !in networkTraktIds) {
                        followedShowsDao.deleteById(localEntry.id)
                    }
                }
            }

            requestManagerRepository.upsert(
                entityId = WATCHLIST_SYNC.requestId,
                requestType = WATCHLIST_SYNC.name,
            )

            traktActivityDao.markAsSynced(SHOWS_WATCHLISTED)
        },
        delete = { _: Unit -> },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = WATCHLIST_SYNC.name,
                threshold = WATCHLIST_SYNC.duration,
            )
        }
    },
).build()

private fun TraktFollowedShowResponse.toFollowedShowEntry(): FollowedShowEntry = FollowedShowEntry(
    traktId = show.ids.trakt,
    tmdbId = show.ids.tmdb,
    followedAt = Instant.parse(listedAt),
    pendingAction = PendingAction.NOTHING,
)
