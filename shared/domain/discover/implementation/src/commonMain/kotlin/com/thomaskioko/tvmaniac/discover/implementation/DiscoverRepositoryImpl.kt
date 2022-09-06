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
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DEFAULT_API_PAGE = 1

class DiscoverRepositoryImpl(
    private val traktService: TraktService,
    private val tmdbService: TmdbService,
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
            onFetchFailed = { Logger.withTag("observeShowsByCategoryID").e { it.resolveError() } },
            coroutineDispatcher = dispatcher
        )
        return networkBoundResource
    }

    override suspend fun fetchDiscoverShows() {
        val trending = traktService.getTrendingShows(DEFAULT_API_PAGE)
        trending.mapAndCacheShows(ShowCategory.TRENDING.type)

        val anticipated = traktService.getAnticipatedShows(DEFAULT_API_PAGE)
        anticipated.mapAndCacheShows(ShowCategory.ANTICIPATED.type)

        val topRatedResponse = traktService.getRecommendedShows(DEFAULT_API_PAGE, "weekely")
        topRatedResponse.mapAndCacheShows(ShowCategory.RECOMMENDED.type)

        val popularResponse = traktService.getPopularShows(DEFAULT_API_PAGE)
        popularResponse.mapAndCacheShow(ShowCategory.POPULAR.type)

    }

    private suspend fun fetchShowsApiRequest(categoryId: Int): List<Show> = when (categoryId) {
        ShowCategory.TRENDING.type -> traktService.getTrendingShows(DEFAULT_API_PAGE)
            .map { it.toShow() }
        ShowCategory.POPULAR.type -> traktService.getPopularShows(DEFAULT_API_PAGE)
            .map { it.toShow() }
        ShowCategory.ANTICIPATED.type -> traktService.getAnticipatedShows(DEFAULT_API_PAGE)
            .map { it.toShow() }
        ShowCategory.RECOMMENDED.type -> traktService.getRecommendedShows(
            DEFAULT_API_PAGE,
            "weekely"
        )
            .map { it.toShow() }
        else -> traktService.getTrendingShows(DEFAULT_API_PAGE).map { it.toShow() }
    }

    private suspend fun cacheResult(
        result: List<Show>,
        categoryId: Int
    ) {
        tvShowCache.insert(result)

        result.forEach { show ->


            //TODO:: Move this out of here
           show.tmdb_id?.let { tmdbId ->
                val response = tmdbService.getTvShowDetails(tmdbId)

                tvShowCache.updateShow(
                    tmdbId = response.id,
                    posterUrl = response.posterPath.toImageUrl(),
                    backdropUrl = response.backdropPath.toImageUrl()
                )
            }

            // Insert Category
            categoryCache.insert(
                Category(
                    id = categoryId,
                    name = ShowCategory[categoryId].title
                )
            )

            // Insert ShowCategory
            discoverCategoryCache.insert(
                Show_category(
                    id = show.trakt_id,
                    category_id = categoryId
                )
            )
        }
    }

    private suspend fun List<TraktShowsResponse>.mapAndCacheShows(
        categoryId: Int,
    ) {
        val result = map { show -> show.toShow() }
        tvShowCache.insert(result)

        result.forEach { show ->

            show.tmdb_id?.let { tmdbId ->
                val response = tmdbService.getTvShowDetails(tmdbId)

                tvShowCache.updateShow(
                    tmdbId = response.id,
                    posterUrl = response.posterPath.toImageUrl(),
                    backdropUrl = response.backdropPath.toImageUrl()
                )
            }

            // Insert Category
            categoryCache.insert(
                Category(
                    id = categoryId,
                    name = ShowCategory[categoryId].title
                )
            )

            // Insert ShowCategory
            discoverCategoryCache.insert(
                Show_category(
                    category_id = categoryId,
                    id = show.trakt_id
                )
            )
        }
    }

    private suspend fun List<TraktShowResponse>.mapAndCacheShow(
        categoryId: Int,
    ) {
        val result = map { show -> show.toShow() }
        tvShowCache.insert(result)

        result.forEach { show ->

            show.tmdb_id?.let { tmdbId ->
                val response = tmdbService.getTvShowDetails(tmdbId)

                tvShowCache.updateShow(
                    tmdbId = response.id,
                    posterUrl = response.posterPath.toImageUrl(),
                    backdropUrl = response.backdropPath.toImageUrl()
                )
            }

            // Insert Category
            categoryCache.insert(
                Category(
                    id = categoryId,
                    name = ShowCategory[categoryId].title
                )
            )

            // Insert ShowCategory
            discoverCategoryCache.insert(
                Show_category(
                    category_id = categoryId,
                    id = show.trakt_id
                )
            )
        }
    }
}
