package com.thomaskioko.tvmaniac.shows.implementation

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
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.RECOMMENDED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.shows.implementation.mapper.toImageUrl
import com.thomaskioko.tvmaniac.shows.implementation.mapper.toShow
import com.thomaskioko.tvmaniac.shows.implementation.mapper.toShowList
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private const val DEFAULT_API_PAGE = 1

@OptIn(FlowPreview::class)
class TmdbRepositoryImpl(
    private val apiService: TmdbService,
    private val tvShowCache: TvShowCache,
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val computationDispatcher: CoroutineDispatcher,
) : TmdbRepository {

    override fun observeShow(tmdbId: Int): Flow<Resource<Show>> = networkBoundResource(
        query = { tvShowCache.observeTvShow(tmdbId) },
        shouldFetch = { it == null || it.status.isBlank() },
        fetch = { apiService.getTvShowDetails(tmdbId) },
        saveFetchResult = { tvShowCache.insert(it.toShow(tmdbId)) },
        onFetchFailed = { Logger.withTag("observeShow").e { it.resolveError() } },
        coroutineDispatcher = dispatcher
    )

    override suspend fun syncShowArtWork() {
        tvShowCache.observeTvShowsArt()
            .flowOn(computationDispatcher)
            .collect { shows ->
                shows.forEach { show ->
                    show.tmdb_id?.let { tmdbId ->
                        val response = apiService.getTvShowDetails(tmdbId)

                        tvShowCache.updateShow(
                            tmdbId = response.id,
                            posterUrl = response.posterPath.toImageUrl(),
                            backdropUrl = response.backdropPath.toImageUrl()
                        )
                    }
                }
            }
    }

    override fun observeUpdateShowArtWork(): Flow<Unit> = tvShowCache.observeTvShowsArt()
        .map { shows ->
            shows.forEach { show ->
                show.tmdb_id?.let { tmdbId ->
                    val response = apiService.getTvShowDetails(tmdbId)

                    tvShowCache.updateShow(
                        tmdbId = response.id,
                        posterUrl = response.posterPath.toImageUrl(),
                        backdropUrl = response.backdropPath.toImageUrl()
                    )
                }
            }
        }
        .flowOn(computationDispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
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
                    items = tvShowCache.getShowsByCategoryID(categoryId).toShowList(),
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
}
