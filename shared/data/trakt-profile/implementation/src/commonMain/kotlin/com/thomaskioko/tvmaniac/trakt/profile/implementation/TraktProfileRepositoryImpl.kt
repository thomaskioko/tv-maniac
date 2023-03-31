package com.thomaskioko.tvmaniac.trakt.profile.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResult
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.profile.api.TraktProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class TraktProfileRepositoryImpl constructor(
    private val traktService: TraktService,
    private val traktListCache: TraktListCache,
    private val statsCache: TraktStatsCache,
    private val traktUserCache: TraktUserCache,
    private val followedCache: TraktFollowedCache,
    private val dateUtilHelper: DateUtilHelper,
    private val dispatcher: CoroutineDispatcher,
) : TraktProfileRepository {

    override fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>> =
        networkBoundResult(
            query = { traktUserCache.observeMe() },
            shouldFetch = { it == null },
            fetch = { traktService.getUserProfile(slug) },
            saveFetchResult = {
                when (it) {
                    is ApiResponse.Success -> {
                        traktUserCache.insert(it.body.toCache(slug))
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
            coroutineDispatcher = dispatcher
        )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Either<Failure, TraktStats>> =
        networkBoundResult(
            query = { statsCache.observeStats() },
            shouldFetch = { it == null || refresh },
            fetch = { traktService.getUserStats(slug) },
            saveFetchResult = { statsCache.insert(it.toCache(slug)) },
            coroutineDispatcher = dispatcher
        )

    override fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_list>> =
        networkBoundResult(
            query = { traktListCache.observeTraktList() },
            shouldFetch = { it == null },
            fetch = { traktService.createFollowingList(userSlug) },
            saveFetchResult = { traktListCache.insert(it.toCache()) },
            coroutineDispatcher = dispatcher
        )

    override fun observeUpdateFollowedShow(
        traktId: Long,
        addToWatchList: Boolean
    ): Flow<Either<Failure, Unit>> = networkBoundResult(
        query = { flowOf(Unit) },
        shouldFetch = { traktUserCache.getMe() != null },
        fetch = {
            val user = traktUserCache.getMe()

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
                        created_at = dateUtilHelper.getTimestampMilliseconds()
                    )
                )

                else -> followedCache.removeShow(traktId)
            }
        },
        coroutineDispatcher = dispatcher
    )

    override suspend fun fetchTraktWatchlistShows() {
        traktUserCache.observeMe()
            .flowOn(dispatcher)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    followedCache.insert(traktService.getWatchList().responseToCache())
                }
            }
    }

    override suspend fun syncFollowedShows() {
        traktUserCache.observeMe()
            .flowOn(dispatcher)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    followedCache.getUnsyncedFollowedShows()
                        .map {

                            traktService.addShowToWatchList(it.id)

                            followedCache.insert(
                                Followed_shows(
                                    id = it.id,
                                    synced = true,
                                    created_at = dateUtilHelper.getTimestampMilliseconds()
                                )
                            )
                        }
                }
            }
    }
}