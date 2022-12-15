package com.thomaskioko.tvmaniac.trakt.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.ANTICIPATED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.implementation.mapper.responseToCache
import com.thomaskioko.tvmaniac.trakt.implementation.mapper.showResponseToCacheList
import com.thomaskioko.tvmaniac.trakt.implementation.mapper.showsResponseToCacheList
import com.thomaskioko.tvmaniac.trakt.implementation.mapper.toCache
import com.thomaskioko.tvmaniac.trakt.implementation.mapper.toCategoryCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


class TraktRepositoryImpl constructor(
    private val tvShowCache: TvShowCache,
    private val traktUserCache: TraktUserCache,
    private val followedCache: TraktFollowedCache,
    private val favoriteCache: TraktListCache,
    private val showCategoryCache: ShowCategoryCache,
    private val statsCache: TraktStatsCache,
    private val traktService: TraktService,
    private val dateUtilHelper: DateUtilHelper,
    private val dispatcher: CoroutineDispatcher,
) : TraktRepository {

    override fun observeMe(slug: String): Flow<Resource<Trakt_user>> =
        networkBoundResource(
            query = { traktUserCache.observeMe() },
            shouldFetch = { it == null },
            fetch = { traktService.getUserProfile(slug) },
            saveFetchResult = { traktUserCache.insert(it.toCache(slug)) },
            onFetchFailed = { Logger.withTag("observeMe").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Resource<TraktStats>> =
        networkBoundResource(
            query = { statsCache.observeStats() },
            shouldFetch = { it == null || refresh },
            fetch = { traktService.getUserStats(slug) },
            saveFetchResult = { statsCache.insert(it.toCache(slug)) },
            onFetchFailed = { Logger.withTag("observeStats").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )


    override fun observeCreateTraktList(userSlug: String): Flow<Resource<Trakt_list>> =
        networkBoundResource(
            query = { favoriteCache.observeTraktList() },
            shouldFetch = { it == null },
            fetch = { traktService.createFollowingList(userSlug) },
            saveFetchResult = { favoriteCache.insert(it.toCache()) },
            onFetchFailed = { Logger.withTag("createTraktFavoriteList").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    override fun observeUpdateFollowedShow(
        traktId: Int,
        addToWatchList: Boolean
    ): Flow<Resource<Unit>> = networkBoundResource(
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
        onFetchFailed = {
            //If something wrong happens on the network layer, we still want to cache it and sync later.
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        id = traktId,
                        synced = false,
                        created_at = dateUtilHelper.getTimestampMilliseconds()
                    )
                )

                else -> followedCache.removeShow(traktId)
            }
            Logger.withTag("observeUpdateFollowedShow").e(it.resolveError())
        },
        coroutineDispatcher = dispatcher
    )

    override fun observeShow(traktId: Int): Flow<Resource<SelectByShowId>> = networkBoundResource(
        query = { tvShowCache.observeTvShow(traktId) },
        shouldFetch = { it == null },
        fetch = { traktService.getSeasonDetails(traktId) },
        saveFetchResult = { response -> tvShowCache.insert(response.responseToCache()) },
        onFetchFailed = { Logger.withTag("observeShow").e { it.resolveError() } },
        coroutineDispatcher = dispatcher
    )

    override fun fetchShowsByCategoryId(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>> =
        networkBoundResource(
            query = { tvShowCache.observeCachedShows(categoryId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { fetchShowsAndMapResult(categoryId) },
            saveFetchResult = { cacheResult(it, categoryId) },
            onFetchFailed = { Logger.withTag("fetchShowsByCategoryId").e(it.resolveError(), it) },
            coroutineDispatcher = dispatcher
        )

    override fun observeCachedShows(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>> =
        tvShowCache.observeCachedShows(ShowCategory[categoryId].id)
            .map { Resource.Success(it) }
            .catch { Resource.Error<List<SelectShowsByCategory>>(it.resolveError()) }

    override suspend fun fetchTraktWatchlistShows() {
        traktUserCache.observeMe()
            .flowOn(dispatcher)
            .collect { user ->
                user?.let {
                    followedCache.insert(traktService.getWatchList().responseToCache())
                }
            }
    }

    override suspend fun fetchShows() {

        val categories = listOf(TRENDING, POPULAR, ANTICIPATED, FEATURED)

        categories.forEach {
            val mappedResult = fetchShowsAndMapResult(it.id)

            tvShowCache.insert(mappedResult)
            showCategoryCache.insert(mappedResult.toCategoryCache(it.id))
        }

    }

    override suspend fun syncFollowedShows() {
        traktUserCache.observeMe()
            .flowOn(dispatcher)
            .collect { user ->
                user?.let {

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

    override suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean) {
        //TODO:: Check if user is signed into trakt and sync followed shows.
        when {
            addToWatchList -> followedCache.insert(
                Followed_shows(
                    id = traktId,
                    synced = false,
                    created_at = dateUtilHelper.getTimestampMilliseconds()
                )
            )

            else -> followedCache.removeShow(traktId)
        }
    }

    override fun observeFollowedShows(): Flow<Resource<List<SelectFollowedShows>>> =
        followedCache.observeFollowedShows()
            .distinctUntilChanged()
            .map { Resource.Success(it) }
            .catch { Resource.Error<List<SelectFollowedShows>>(it.resolveError()) }

    override fun getFollowedShows(): List<SelectFollowedShows> = followedCache.getFollowedShows()

    private suspend fun fetchShowsAndMapResult(categoryId: Int): List<Show> =
        //TODO:: Improve error handling
        try {
            when (categoryId) {
                POPULAR.id -> traktService.getPopularShows().showResponseToCacheList()
                TRENDING.id -> traktService.getTrendingShows().showsResponseToCacheList()
                ANTICIPATED.id -> traktService.getAnticipatedShows().showsResponseToCacheList()
                FEATURED.id -> traktService.getRecommendedShows(period = "daily")
                    .showsResponseToCacheList()
                else -> throw Throwable("Unsupported type sunny")
            }
        } catch (exception: Throwable){
            Logger.withTag("fetchShowsAndMapResult").e(exception.resolveError(), exception)
            emptyList()
        }


    private fun cacheResult(result: List<Show>, categoryId: Int) {
        tvShowCache.insert(result)

        showCategoryCache.insert(result.toCategoryCache(categoryId))
    }
}