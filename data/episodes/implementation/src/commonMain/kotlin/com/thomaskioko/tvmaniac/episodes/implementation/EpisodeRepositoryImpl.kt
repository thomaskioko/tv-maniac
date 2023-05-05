package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.Episode_image
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class EpisodeRepositoryImpl(
    private val tmdbService: TmdbService,
    private val episodesCache: EpisodesCache,
    private val episodeImageCache: EpisodeImageCache,
    private val formatterUtil: FormatterUtil,
    private val logger: KermitLogger,
    private val exceptionHandler: ExceptionHandler,
) : EpisodeRepository {

    override fun updateEpisodeArtWork(): Flow<Either<Failure, Unit>> =
        episodesCache.observeEpisodeArtByShowId()
            .map { episode ->
                episode.forEach { episodeArt ->
                    episodeArt.tmdb_id?.let { tmdbId ->
                        val response = tmdbService.getEpisodeDetails(
                            tmdbShow = tmdbId,
                            ssnNumber = episodeArt.season_number!!,
                            epNumber = episodeArt.episode_number.toLong(),
                        )

                        when (response) {
                            is ApiResponse.Success -> {
                                episodeImageCache.insert(
                                    Episode_image(
                                        trakt_id = episodeArt.trakt_id,
                                        tmdb_id = response.body.id.toLong(),
                                        image_url = formatterUtil.formatTmdbPosterPath(response.body.imageUrl),
                                    ),
                                )
                            }

                            is ApiResponse.Error -> {
                                logger.error("updateEpisodeArtWork", "$response")
                            }
                        }
                    }
                }

                Either.Right(Unit)
            }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
}
