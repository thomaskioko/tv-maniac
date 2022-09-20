package com.thomaskioko.tvmaniac.discover.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Category
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiscoverRepositoryImpl(
    private val traktService: TraktService,
    private val tmdbService: TmdbService,
    private val tvShowCache: TvShowCache,
    private val showCategoryCache: ShowCategoryCache,
    private val categoryCache: CategoryCache,
    private val dispatcher: CoroutineDispatcher,
) : DiscoverRepository {

    override fun observeShowsByCategoryID(categoryId: Int): Flow<Resource<List<Show>>> {
        val networkBoundResource = networkBoundResource(
            query = {
                tvShowCache.observeShowsByCategoryID(categoryId)
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
        val trending = traktService.getTrendingShows()
        trending.mapAndCacheShows(ShowCategory.TRENDING.type)

        val anticipated = traktService.getAnticipatedShows()
        anticipated.mapAndCacheShows(ShowCategory.ANTICIPATED.type)

        val topRatedResponse = traktService.getRecommendedShows(period = "weekely")
        topRatedResponse.mapAndCacheShows(ShowCategory.RECOMMENDED.type)

        val popularResponse = traktService.getPopularShows()
        popularResponse.mapAndCacheShow(ShowCategory.POPULAR.type)

        val featuredResponse = traktService.getRecommendedShows(period = "monthly",)
        featuredResponse.mapAndCacheShows(ShowCategory.FEATURED.type)

    }

    private suspend fun fetchShowsApiRequest(categoryId: Int): List<Show> = when (categoryId) {
        ShowCategory.TRENDING.type -> traktService.getTrendingShows()
            .map { it.toShow() }
        ShowCategory.POPULAR.type -> traktService.getPopularShows()
            .map { it.toShow() }
        ShowCategory.ANTICIPATED.type -> traktService.getAnticipatedShows()
            .map { it.toShow() }
        ShowCategory.RECOMMENDED.type -> traktService.getRecommendedShows(period = "weekely")
            .map { it.toShow() }
        ShowCategory.FEATURED.type -> traktService.getRecommendedShows(period = "monthly")
            .map { it.toShow() }
        else -> traktService.getTrendingShows().map { it.toShow() }
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
            showCategoryCache.insert(
                Show_category(
                    category_id = categoryId,
                    trakt_id = show.trakt_id
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
            showCategoryCache.insert(
                Show_category(
                    category_id = categoryId,
                    trakt_id = show.trakt_id
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
            showCategoryCache.insert(
                Show_category(
                    category_id = categoryId,
                    trakt_id = show.trakt_id
                )
            )
        }
    }
}
