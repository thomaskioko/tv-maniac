package com.thomaskioko.tvmaniac.showimages.implementation

import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesDao
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class ShowImagesRepositoryImpl(
    private val networkDataSource: TmdbNetworkDataSource,
    private val imageCache: ShowImagesDao,
    private val formatterUtil: FormatterUtil,
    private val exceptionHandler: NetworkExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: KermitLogger,
) : ShowImagesRepository {

    override fun updateShowArtWork(): Flow<Either<Failure, Unit>> =
        imageCache.observeShowImages()
            .map { shows ->
                shows.forEach { show ->
                    show.tmdb_id?.let { tmdbId ->

                        when (val response = networkDataSource.getTvShowDetails(tmdbId)) {
                            is ApiResponse.Error -> {
                                logger.error("updateShowArtWork", "$response")
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
