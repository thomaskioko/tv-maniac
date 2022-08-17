package com.thomaskioko.tvmaniac.discover.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Category
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.DiscoverCategoryCache
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.remote.api.model.TvShowsResponse
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DEFAULT_API_PAGE = 1

class DiscoverRepositoryImpl(
    private val apiService: TmdbService,
    private val tvShowCache: TvShowCache,
    private val discoverCategoryCache: DiscoverCategoryCache,
    private val categoryCache: CategoryCache,
    private val dispatcher: CoroutineDispatcher,
) : DiscoverRepository {

    override fun observeShowsByCategoryID(categoryId: Int): Flow<Resource<List<Show>>> {
        val networkBoundResource = networkBoundResource(
            query = {
                discoverCategoryCache.observeShowsByCategoryID(categoryId)
                    .map { it.toShowList() }
            },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { fetchShowsApiRequest(categoryId) },
            saveFetchResult = { cacheResult(it, categoryId) },
            onFetchFailed = {
                Logger.withTag("observeShowsByCategoryID").e { it.resolveError() }
            },
            coroutineDispatcher = dispatcher
        )
        return networkBoundResource
    }

    override suspend fun fetchDiscoverShows() {
        val response = fetchShowsApiRequest(ShowCategory.TRENDING.type)
        cacheResult(response, ShowCategory.TRENDING.type)

        val topRatedResponse = fetchShowsApiRequest(ShowCategory.TOP_RATED.type)
        cacheResult(topRatedResponse, ShowCategory.TOP_RATED.type)

        val popularResponse = fetchShowsApiRequest(ShowCategory.POPULAR.type)
        cacheResult(popularResponse, ShowCategory.POPULAR.type)

    }

    private suspend fun fetchShowsApiRequest(categoryId: Int): TvShowsResponse = when (categoryId) {
        ShowCategory.TRENDING.type -> apiService.getTrendingShows(DEFAULT_API_PAGE)
        ShowCategory.POPULAR.type -> apiService.getPopularShows(DEFAULT_API_PAGE)
        ShowCategory.TOP_RATED.type -> apiService.getTopRatedShows(DEFAULT_API_PAGE)
        else -> apiService.getTrendingShows(DEFAULT_API_PAGE)
    }

    private fun cacheResult(
        it: TvShowsResponse,
        categoryId: Int
    ) {
        val result = it.results.map { show -> show.toShow() }
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
            discoverCategoryCache.insert(
                Show_category(
                    category_id = categoryId.toLong(),
                    show_id = show.id
                )
            )
        }
    }
}
