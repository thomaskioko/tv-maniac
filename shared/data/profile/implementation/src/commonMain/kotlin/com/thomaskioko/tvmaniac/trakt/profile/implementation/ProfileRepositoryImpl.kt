package com.thomaskioko.tvmaniac.trakt.profile.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.Trakt_shows_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.FavoriteListCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.StatsCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.UserCache
import com.thomaskioko.tvmaniac.util.DateFormatter
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileRepositoryImpl constructor(
    private val traktService: TraktService,
    private val favoriteListCache: FavoriteListCache,
    private val statsCache: StatsCache,
    private val userCache: UserCache,
    private val followedCache: FollowedCache,
    private val dateFormatter: DateFormatter,
    private val mapper: ProfileResponseMapper,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : ProfileRepository {

    override fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>> =
        networkBoundResult(
            query = { userCache.observeMe() },
            shouldFetch = { it == null },
            fetch = { traktService.getUserProfile(slug) },
            saveFetchResult = {
                when (it) {
                    is ApiResponse.Success -> {
                        userCache.insert(mapper.toTraktList(slug, it.body))
                    }

                    is ApiResponse.Error.GenericError -> {
                        Logger.withTag("observeMe").e("$it")
                        throw Throwable("${it.errorMessage}")
                    }

                    is ApiResponse.Error.HttpError -> {
                        Logger.withTag("observeMe").e("$it")
                        throw Throwable("${it.code} - ${it.errorBody?.message}")
                    }

                    is ApiResponse.Error.SerializationError -> {
                        Logger.withTag("observeMe").e("$it")
                        throw Throwable("$it")
                    }
                }
            },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Either<Failure, User_stats>> =
        networkBoundResult(
            query = { statsCache.observeStats() },
            shouldFetch = { it == null || refresh },
            fetch = { traktService.getUserStats(slug) },
            saveFetchResult = { statsCache.insert(mapper.toTraktStats(slug, it)) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_shows_list>> =
        networkBoundResult(
            query = { favoriteListCache.observeTraktList() },
            shouldFetch = { it == null },
            fetch = { traktService.createFollowingList(userSlug) },
            saveFetchResult = { favoriteListCache.insert(mapper.toTraktList(it)) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeUpdateFollowedShow(
        traktId: Long,
        addToWatchList: Boolean
    ): Flow<Either<Failure, Unit>> = networkBoundResult(
        query = { flowOf(Unit) },
        shouldFetch = { userCache.getMe() != null },
        fetch = {
            val user = userCache.getMe()

            if (user != null) {
                if (addToWatchList) {
                    traktService.addShowToWatchList(traktId).added.shows
                } else {
                    traktService.removeShowFromWatchList(traktId).deleted.shows
                }
            }
        },
        saveFetchResult = {
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        id = traktId,
                        synced = true,
                        created_at = dateFormatter.getTimestampMilliseconds()
                    )
                )

                else -> followedCache.removeShow(traktId)
            }
        },
        exceptionHandler = exceptionHandler,
        coroutineDispatcher = dispatchers.io
    )

    override suspend fun fetchTraktWatchlistShows() {
        userCache.observeMe()
            .flowOn(dispatchers.io)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    followedCache.insert(mapper.responseToCache(traktService.getWatchList()))
                }
            }
    }

    override suspend fun syncFollowedShows() {
        userCache.observeMe()
            .flowOn(dispatchers.io)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    followedCache.getUnsyncedFollowedShows()
                        .map {

                            traktService.addShowToWatchList(it.id)

                            followedCache.insert(
                                Followed_shows(
                                    id = it.id,
                                    synced = true,
                                    created_at = dateFormatter.getTimestampMilliseconds()
                                )
                            )
                        }
                }
            }
    }
}