package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.USER_STATS
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class UserStatsStore(
    private val traktUserRemoteDataSource: TraktUserRemoteDataSource,
    private val userStatsDao: UserStatsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, UserProfileStats> by storeBuilder(
    fetcher = apiFetcher { slug ->
        traktUserRemoteDataSource.getUserStats(slug)
    },
    sourceOfTruth = SourceOfTruth.of<String, TraktUserStatsResponse, UserProfileStats>(
        reader = { slug -> userStatsDao.observeUserProfileStats(slug) },
        writer = { slug, response ->
            withContext(dispatchers.databaseWrite) {
                userStatsDao.upsertStats(
                    slug = slug,
                    showsWatched = response.shows.watched.toLong(),
                    episodesWatched = response.episodes.watched.toLong(),
                    minutesWatched = (response.episodes.minutes + response.movies.minutes).toLong(),
                )

                requestManagerRepository.upsert(
                    entityId = USER_STATS.requestId,
                    requestType = USER_STATS.name,
                )
            }
        },
        deleteAll = { userStatsDao.deleteAll() },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { _ ->
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = USER_STATS.name,
                threshold = USER_STATS.duration,
            )
        }
    },
).build()
