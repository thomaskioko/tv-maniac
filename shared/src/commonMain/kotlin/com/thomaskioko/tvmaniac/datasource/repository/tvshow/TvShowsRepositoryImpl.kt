package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TODAY
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow.WEEK
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.CommonFlow
import com.thomaskioko.tvmaniac.util.asCommonFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(FlowPreview::class)
@ExperimentalCoroutinesApi
class TvShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val cache: TvShowCache,
    private val coroutineScope: CoroutineScope,
) : TvShowsRepository {

    override fun getTvShow(tvShowId: Int): Flow<TvShow> {
        return cache.getTvShow(tvShowId)
            .map { it.toTvShow() }
    }

    override suspend fun getPopularTvShows(page: Int): List<TvShow> {
        return if (getShowsByCategory(POPULAR).isEmpty()) {

            mapApiResultAndInsert(apiService.getPopularShows(page), POPULAR)

            getShowsByCategory(POPULAR)
        } else {
            getShowsByCategory(POPULAR)
        }
    }

    override suspend fun getPagedPopularTvShows(): CommonFlow<PagingData<TvShow>> {
        val pager = Pager(
            clientScope = coroutineScope,
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            initialKey = 1,
            getItems = { currentKey, _ ->
                val apiResponse = apiService.getPopularShows(currentKey)

                mapApiResultAndInsert(apiResponse, POPULAR)

                val tvShows = getShowsByCategory(POPULAR)
                PagingResult(
                    items = tvShows,
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { apiResponse.page + 1 }
                )
            }
        )

        return pager.pagingData
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    override suspend fun getTopRatedTvShows(page: Int): List<TvShow> {
        return if (getShowsByCategory(TOP_RATED).isEmpty()) {

            apiService.getTopRatedShows(page).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = TOP_RATED
                    )
                }
                .map { cache.insert(it) }

            getShowsByCategory(TOP_RATED)
        } else {
            getShowsByCategory(TOP_RATED)
        }
    }

    override suspend fun getPagedTopRatedTvShows(): CommonFlow<PagingData<TvShow>> {
        val pager = Pager(
            clientScope = coroutineScope,
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            initialKey = 1,
            getItems = { currentKey, _ ->
                val apiResponse = apiService.getTopRatedShows(currentKey)

                mapApiResultAndInsert(apiResponse, TOP_RATED)

                val tvShows = getShowsByCategory(TOP_RATED)

                PagingResult(
                    items = tvShows,
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { apiResponse.page + 1 }
                )
            }
        )

        return pager.pagingData
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    override suspend fun getTrendingShowsByTime(timeWindow: TimeWindow): List<TvShow> {
        return if (getShowsByCategoryAndWindow(TRENDING, timeWindow).isEmpty()) {

            val cacheResult = apiService.getTrendingShows(1, timeWindow.window).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = TRENDING,
                        time_window = timeWindow
                    )
                }

            cache.insert(cacheResult)

            getShowsByCategoryAndWindow(TRENDING, timeWindow)
        } else {
            getShowsByCategoryAndWindow(TRENDING, timeWindow)
        }
    }

    override suspend fun getFeaturedShows(): List<TvShow> {
        return if (getShowsByCategoryAndWindow(FEATURED, WEEK).isEmpty()) {

            apiService.getTrendingShows(1, WEEK.window).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = FEATURED,
                        time_window = WEEK
                    )
                }
                .map { cache.insert(it) }

            cache.getFeaturedTvShows(FEATURED, WEEK).toTvShowList()
        } else {
            cache.getFeaturedTvShows(FEATURED, WEEK).toTvShowList()
        }
    }

    override suspend fun getShowsByCategory(category: ShowCategory): List<TvShow> {
        return cache.getTvShowsByCategory(category)
            .toTvShowList()
    }


    override suspend fun getPagedShowsByCategory(category: ShowCategory): CommonFlow<PagingData<TvShow>> {
        val pager = Pager(
            clientScope = coroutineScope,
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            initialKey = 1,
            getItems = { currentKey, _ ->
                PagingResult(
                    items = getShowsByCategory(category),
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { 0 + 1 }
                )
            }
        )

        return pager.pagingData
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    override fun getWatchlist(): Flow<List<TvShow>> = cache.getWatchlist()
        .map { it.toTvShowList() }

    override suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean) {
        cache.updateWatchlist(showId, addToWatchList)
    }

    override suspend fun getShowsByCategoryAndWindow(
        category: ShowCategory,
        timeWindow: TimeWindow
    ): List<TvShow> {
        val result = cache.getTvShows(category, timeWindow)
            .toTvShowList()
        Napier.d("Query size ${result.size}")
        return result
    }

    override suspend fun getPagedShowsByCategoryAndWindow(
        category: ShowCategory,
        timeWindow: TimeWindow
    ): CommonFlow<PagingData<TvShow>> {
        val pager = Pager(
            clientScope = coroutineScope,
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                initialLoadSize = 30
            ),
            initialKey = 2,
            getItems = { currentKey, _ ->

                val apiResponse = when (category) {
                    FEATURED, TODAY, THIS_WEEK, TRENDING ->
                        apiService.getTrendingShows(currentKey, timeWindow.window)
                    TOP_RATED -> apiService.getTopRatedShows(currentKey)
                    POPULAR -> apiService.getPopularShows(currentKey)
                }

                val cacheResult = apiResponse.results
                    .map { it.toShow() }
                    .map {
                        it.copy(
                            show_category = category,
                            time_window = timeWindow
                        )
                    }

                cache.insert(cacheResult)


                val tvShows = getShowsByCategoryAndWindow(category, timeWindow)

                Napier.d("Updated Query size ${tvShows.size}")

                PagingResult(
                    items = tvShows,
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { apiResponse.page + 1 }
                )
            }
        )

        return pager.pagingData
            .distinctUntilChanged()
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    private fun mapApiResultAndInsert(apiResponse: TvShowsResponse, category: ShowCategory) {
        val entityList = apiResponse.results
            .map { it.toShow() }
            .map {
                it.copy(
                    show_category = category
                )
            }

        cache.insert(entityList)
    }

}
