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
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toShowList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import com.thomaskioko.tvmaniac.datasource.repository.util.Resource
import com.thomaskioko.tvmaniac.datasource.repository.util.networkBoundResource
import com.thomaskioko.tvmaniac.util.CommonFlow
import com.thomaskioko.tvmaniac.util.Logger
import com.thomaskioko.tvmaniac.util.asCommonFlow
import com.thomaskioko.tvmaniac.util.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val DEFAULT_API_PAGE = 1

class TvShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val tvShowCache: TvShowCache,
    private val categoryCache: CategoryCache,
    private val showCategoryCache: ShowCategoryCache,
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : TvShowsRepository {

    override fun observeShow(tvShowId: Int): Flow<Resource<Show>> = networkBoundResource(
        query = { tvShowCache.getTvShow(tvShowId) },
        shouldFetch = { it == null },
        fetch = { apiService.getTvShowDetails(tvShowId) },
        saveFetchResult = { tvShowCache.insert(it.toShow()) },
        onFetchFailed = { Logger("observeShow").log(it.getErrorMessage()) },
        coroutineDispatcher = dispatcher
    )

    override suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean) {
        tvShowCache.updateWatchlist(showId, addToWatchList)
    }

    override fun observeWatchlist(): Flow<List<Show>> = tvShowCache.getWatchlist()

    override fun observeShowsByCategoryID(categoryId: Int): Flow<Resource<List<Show>>> =
        networkBoundResource(
            query = {
                showCategoryCache.observeShowsByCategoryID(categoryId)
                    .map { it.toShowList() }
            },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { fetchShowsApiRequest(categoryId) },
            saveFetchResult = { cacheResult(it, categoryId) },
            onFetchFailed = { Logger("TvShowsRepository").log(it.getErrorMessage()) },
            coroutineDispatcher = dispatcher
        )

    override fun observePagedShowsByCategoryID(
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
                    TRENDING.type -> apiService.getTrendingShows(currentKey)
                    TOP_RATED.type -> apiService.getTopRatedShows(currentKey)
                    POPULAR.type -> apiService.getPopularShows(currentKey)
                    else -> apiService.getTrendingShows(currentKey)
                }

                apiResponse.results
                    .map { tvShowCache.insert(it.toShow()) }

                PagingResult(
                    items = showCategoryCache.getShowsByCategoryID(categoryId).toShowList(),
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

    private suspend fun fetchShowsApiRequest(categoryId: Int): TvShowsResponse = when (categoryId) {
        TRENDING.type -> apiService.getTrendingShows(DEFAULT_API_PAGE)
        POPULAR.type -> apiService.getPopularShows(DEFAULT_API_PAGE)
        TOP_RATED.type -> apiService.getTopRatedShows(DEFAULT_API_PAGE)
        else -> apiService.getTrendingShows(DEFAULT_API_PAGE)
    }

    private fun cacheResult(
        it: TvShowsResponse,
        categoryId: Int
    ) {
        val result = it.results.map { show ->
            show.toShow()
        }
        tvShowCache.insert(result)

        result.forEach { show ->
            // Insert Category
            categoryCache.insert(
                Category(
                    id = categoryId.toLong(),
                    name = ShowCategory[categoryId].title
                )
            )

            // Insert ShowCategory
            showCategoryCache.insert(
                Show_category(
                    category_id = categoryId.toLong(),
                    show_id = show.id
                )
            )
        }
    }
}
