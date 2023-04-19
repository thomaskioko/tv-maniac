package com.thomaskioko.tvmaniac.tmdb.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class TmdbRepositoryImpl(
    private val apiService: TmdbService,
    private val showsCache: ShowsCache,
    private val imageCache: ShowImageCache,
    private val formatterUtil: FormatterUtil,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : TmdbRepository {

    override fun updateShowArtWork(): Flow<Either<Failure, Unit>> =
        showsCache.observeShowImages()
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
                                        poster_url = formatterUtil.formatTmdbPosterPath(response.body.posterPath),
                                        backdrop_url = formatterUtil.formatTmdbPosterPath(response.body.backdropPath),
                                    ),
                                )
                            }
                        }
                    }
                }

                Either.Right(Unit)
            }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
            .flowOn(dispatchers.io)
}
