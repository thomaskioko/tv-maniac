package com.thomaskioko.tvmaniac.shows.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.FormatterUtil.formatPosterPath
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.shows.implementation.mapper.toShow
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


class TmdbRepositoryImpl(
    private val apiService: TmdbService,
    private val tvShowCache: TvShowCache,
    private val imageCache: ShowImageCache,
    private val dispatcher: CoroutineDispatcher,
) : TmdbRepository {

    override fun observeShow(tmdbId: Int): Flow<Resource<SelectByShowId>> = networkBoundResource(
        query = { tvShowCache.observeTvShow(tmdbId) },
        shouldFetch = { it == null || it.backdrop_url.isNullOrBlank() },
        fetch = { apiService.getTvShowDetails(tmdbId) },
        saveFetchResult = { tvShowCache.insert(it.toShow(tmdbId)) },
        onFetchFailed = { Logger.withTag("observeShow").e { it.resolveError() } },
        coroutineDispatcher = dispatcher
    )

    override fun observeUpdateShowArtWork(): Flow<Unit> = tvShowCache.observeTvShows()
        .map { shows ->
            shows
                .filter {
                    it.poster_url.isNullOrEmpty() || it.backdrop_url.isNullOrEmpty()
                }
                .forEach { show ->
                    show.tmdb_id?.let { tmdbId ->
                        val response = apiService.getTvShowDetails(tmdbId)

                        imageCache.insert(
                            Show_image(
                                trakt_id = show.trakt_id,
                                poster_url = response.posterPath.toImageUrl(),
                                backdrop_url = response.backdropPath.toImageUrl()
                            )
                        )
                    }
                }
        }
        .flowOn(dispatcher)
}

fun String?.toImageUrl() = formatPosterPath(this)
