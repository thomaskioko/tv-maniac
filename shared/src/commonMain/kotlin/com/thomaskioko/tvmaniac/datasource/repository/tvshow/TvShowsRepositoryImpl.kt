package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.thomaskioko.tvmaniac.datasource.cache.Category
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.Show_category
import com.thomaskioko.tvmaniac.datasource.cache.category.CategoryCache
import com.thomaskioko.tvmaniac.datasource.cache.show_category.ShowCategoryCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import com.thomaskioko.tvmaniac.datasource.repository.TrendingShowData
import com.thomaskioko.tvmaniac.util.CommonFlow
import com.thomaskioko.tvmaniac.util.asCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DEFAULT_API_PAGE = 1
private const val FEATURED_LIST_SIZE = 5

class TvShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val tvShowCache: TvShowCache,
    private val categoryCache: CategoryCache,
    private val showCategoryCache: ShowCategoryCache,
    private val coroutineScope: CoroutineScope,
) : TvShowsRepository {

    override fun getShow(tvShowId: Int): Flow<Show> = tvShowCache.getTvShow(tvShowId)

    override suspend fun getShowsByCategoryId(page: Int, categoryId: Int): List<Show> {
        return if (getCachedShowsByCategoryId(categoryId).isEmpty()) {

            val apiResponse = when (categoryId) {
                TRENDING.type -> apiService.getTrendingShows(page)
                POPULAR.type -> apiService.getPopularShows(page)
                TOP_RATED.type -> apiService.getTopRatedShows(page)
                else -> apiService.getTopRatedShows(page)
            }

            mapApiResultAndInsert(apiResponse, ShowCategory[categoryId])

            getCachedShowsByCategoryId(categoryId)
        } else {
            getCachedShowsByCategoryId(categoryId)
        }
    }

    override suspend fun getDiscoverShowList(
        categoryList: List<ShowCategory>
    ): List<TrendingShowData> {
        val trendingShowsResult = mutableListOf<TrendingShowData>()

        categoryList.forEach { category ->
            val result = when (category) {
                TRENDING -> getShowsByCategoryId(
                    page = DEFAULT_API_PAGE,
                    categoryId = category.type
                )
                POPULAR -> getShowsByCategoryId(
                    page = DEFAULT_API_PAGE,
                    categoryId = category.type
                )
                TOP_RATED -> getShowsByCategoryId(
                    page = DEFAULT_API_PAGE,
                    categoryId = category.type
                )
                FEATURED -> getShowsByCategoryId(
                    page = DEFAULT_API_PAGE,
                    categoryId = TRENDING.type
                ).take(FEATURED_LIST_SIZE)
            }.map { it.toTvShow() }

            trendingShowsResult.add(
                TrendingShowData(
                    category = category,
                    shows = result
                )
            )
        }

        return trendingShowsResult
    }

    override suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean) {
        tvShowCache.updateWatchlist(showId, addToWatchList)
    }

    override fun getWatchlist(): Flow<List<Show>> = tvShowCache.getWatchlist()

    override fun getPagedPopularTvShows(): CommonFlow<PagingData<Show>> {
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

                val tvShows = getCachedShowsByCategoryId(POPULAR.type)
                PagingResult(
                    items = tvShows,
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { apiResponse.page + DEFAULT_API_PAGE }
                )
            }
        )

        return pager.pagingData
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    override fun getPagedTopRatedTvShows(): CommonFlow<PagingData<Show>> {
        val pager = Pager(
            clientScope = coroutineScope,
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            initialKey = DEFAULT_API_PAGE,
            getItems = { currentKey, _ ->
                val apiResponse = apiService.getTopRatedShows(currentKey)

                mapApiResultAndInsert(apiResponse, TOP_RATED)

                PagingResult(
                    items = getCachedShowsByCategoryId(TOP_RATED.type),
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { apiResponse.page + DEFAULT_API_PAGE }
                )
            }
        )

        return pager.pagingData
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    override fun getPagedShowsByCategoryAndWindow(
        categoryId: Int
    ): CommonFlow<PagingData<Show>> {
        val pager = Pager(
            clientScope = coroutineScope,
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                initialLoadSize = 30
            ),
            initialKey = 2,
            getItems = { currentKey, _ ->

                val apiResponse = when (categoryId) {
                    FEATURED.type, TRENDING.type -> apiService.getTrendingShows(currentKey)
                    TOP_RATED.type -> apiService.getTopRatedShows(currentKey)
                    POPULAR.type -> apiService.getPopularShows(currentKey)
                    else -> apiService.getPopularShows(currentKey)
                }

                apiResponse.results
                    .map { tvShowCache.insert(it.toShow()) }

                PagingResult(
                    items = getCachedShowsByCategoryId(categoryId),
                    currentKey = currentKey,
                    prevKey = { null },
                    nextKey = { apiResponse.page + DEFAULT_API_PAGE }
                )
            }
        )

        return pager.pagingData
            .distinctUntilChanged()
            .cachedIn(coroutineScope)
            .asCommonFlow()
    }

    private fun getCachedShowsByCategoryId(categoryId: Int): List<Show> =
        showCategoryCache.getShowsByCategoryID(categoryId)
            .map { it.toTvShow() }

    private fun mapApiResultAndInsert(apiResponse: TvShowsResponse, category: ShowCategory) {

        val cacheList = apiResponse.results
            .map { it.toShow() }

        // Insert shows
        tvShowCache.insert(cacheList)

        cacheList.forEach { show ->
            // Insert Category
            categoryCache.insert(
                Category(
                    id = category.type.toLong(),
                    name = category.title
                )
            )

            // Insert ShowCategory
            showCategoryCache.insert(
                Show_category(
                    category_id = category.type.toLong(),
                    show_id = show.id
                )
            )
        }
    }
}
