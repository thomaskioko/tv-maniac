package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.USER_STATS
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class UserStatsStore(
    private val activeSource: () -> UserRemoteDataSource?,
    private val userStatsDao: UserStatsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, UserProfileStats> by storeBuilder(
    fetcher = Fetcher.ofResult { slug: String ->
        when (val response = activeSource()?.getUserStats(slug) ?: ApiResponse.Unauthenticated) {
            is ApiResponse.Success -> when (val body = response.body) {
                null -> FetcherResult.Error.Exception(AuthenticationException("Provider does not support user stats"))
                else -> FetcherResult.Data(body)
            }
            is ApiResponse.Unauthenticated -> FetcherResult.Error.Exception(AuthenticationException("Not authenticated"))
            is ApiResponse.Error ->
                if (response is ApiResponse.Error.HttpError && response.code == 404) {
                    FetcherResult.Error.Exception(AuthenticationException("User stats unavailable (HTTP 404)"))
                } else {
                    FetcherResult.Error.Exception(SyncException(response.toSyncError()))
                }
        }
    },
    sourceOfTruth = SourceOfTruth.of<String, RemoteUserStats, UserProfileStats>(
        reader = { slug -> userStatsDao.observeUserProfileStats(slug) },
        writer = { slug, response ->
            withContext(dispatchers.databaseWrite) {
                userStatsDao.upsertStats(
                    slug = slug,
                    showsWatched = response.showsWatched,
                    episodesWatched = response.episodesWatched,
                    minutesWatched = response.minutesWatched,
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
