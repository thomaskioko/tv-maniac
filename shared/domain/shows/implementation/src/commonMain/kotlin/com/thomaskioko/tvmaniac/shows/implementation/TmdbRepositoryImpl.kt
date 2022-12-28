package com.thomaskioko.tvmaniac.shows.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.util.FormatterUtil.formatPosterPath
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


class TmdbRepositoryImpl(
    private val apiService: TmdbService,
    private val tvShowCache: TvShowCache,
    private val imageCache: ShowImageCache,
    private val dispatcher: CoroutineDispatcher,
) : TmdbRepository {

    override fun updateShowArtWork(): Flow<Either<Failure, Unit>> =
        tvShowCache.observeShowImages()
            .map { shows ->
                shows.forEach { show ->
                    show.tmdb_id?.let { tmdbId ->

                        when (val response = apiService.getTvShowDetails(tmdbId)) {
                            is ApiResponse.Error -> {
                                Logger.withTag("updateShowArtWork")
                                    .e("$response")
                            }

                            is ApiResponse.Success -> {
                                imageCache.insert(
                                    Show_image(
                                        trakt_id = show.trakt_id,
                                        tmdb_id = tmdbId,
                                        poster_url = formatPosterPath(response.body.posterPath),
                                        backdrop_url = formatPosterPath(response.body.backdropPath)
                                    )
                                )
                            }
                        }
                    }
                }

                Either.Right(Unit)
            }
            .catch { Either.Left(DefaultError(it)) }
            .flowOn(dispatcher)
}
