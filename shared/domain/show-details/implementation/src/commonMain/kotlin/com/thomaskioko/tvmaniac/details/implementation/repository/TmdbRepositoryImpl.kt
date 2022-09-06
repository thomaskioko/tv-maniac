package com.thomaskioko.tvmaniac.details.implementation.repository

import co.touchlab.kermit.Logger
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.CommonFlow
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.asCommonFlow
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.details.implementation.mapper.toAirEp
import com.thomaskioko.tvmaniac.details.implementation.mapper.toImageUrl
import com.thomaskioko.tvmaniac.details.implementation.mapper.toShow
import com.thomaskioko.tvmaniac.details.implementation.mapper.toShowList
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory.RECOMMENDED
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DEFAULT_API_PAGE = 1

//TODO:: Move this to common implementation module
class TmdbRepositoryImpl(
    private val apiService: TmdbService,
    private val tvShowCache: TvShowCache,
    private val epAirCacheLast: LastEpisodeAirCache,
    private val similarShowCache: SimilarShowCache,
    private val showCategoryCache: ShowCategoryCache,
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : TmdbRepository {

    override fun observeShow(tmdbId: Int): Flow<Resource<Show>> = networkBoundResource(
        query = { tvShowCache.observeTvShow(tmdbId) },
        shouldFetch = { it == null || it.status.isBlank() },
        fetch = { apiService.getTvShowDetails(tmdbId) },
        saveFetchResult = { mapAndInsert(tmdbId, it) },
        onFetchFailed = { Logger.withTag("observeShow").e { it.resolveError() } },
        coroutineDispatcher = dispatcher
    )

    override suspend fun syncShowArtWork() {
        tvShowCache.observeTvShowsArt()
            .collect { shows ->
                shows.forEach { show ->
                    show.tmdb_id?.let { tmdbId ->
                        val response = apiService.getTvShowDetails(tmdbId)

                        tvShowCache.updateShow(
                            tmdbId = response.id,
                            posterUrl = response.posterPath.toImageUrl(),
                            backdropUrl = response.backdropPath.toImageUrl()
                        )

                        similarShowCache.updateShow(show.trakt_id)
                    }
                }
            }
    }

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
                    RECOMMENDED.type -> apiService.getTopRatedShows(currentKey)
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

    private fun mapAndInsert(tvShowId: Int, response: ShowDetailResponse) {
        tvShowCache.insert(response.toShow(tvShowId))

        response.lastEpisodeToAir?.let {
            epAirCacheLast.insert(it.toAirEp(tvShowId))
        }

        response.nextEpisodeToAir?.let {
            epAirCacheLast.insert(it.toAirEp(tvShowId))
        }
    }
}
