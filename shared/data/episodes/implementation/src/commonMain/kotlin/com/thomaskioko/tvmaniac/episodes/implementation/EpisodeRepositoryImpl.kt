package com.thomaskioko.tvmaniac.episodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Episode_image
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class EpisodeRepositoryImpl(
    private val tmdbService: TmdbService,
    private val episodesCache: EpisodesCache,
    private val episodeImageCache: EpisodeImageCache,
) : EpisodeRepository {

    override fun updateEpisodeArtWork(): Flow<Either<Failure, Unit>> =
        episodesCache.observeEpisodeArtByShowId()
            .map { episode ->
                episode.forEach { episodeArt ->
                    episodeArt.tmdb_id?.let { tmdbId ->
                        val response = tmdbService.getEpisodeDetails(
                            tmdbShow = tmdbId,
                            ssnNumber = episodeArt.season_number!!,
                            epNumber = episodeArt.episode_number.toLong()
                        )

                        when (response) {
                            is ApiResponse.Success -> {
                                episodeImageCache.insert(
                                    Episode_image(
                                        trakt_id = episodeArt.trakt_id,
                                        tmdb_id = response.body.id.toLong(),
                                        image_url = response.body.imageUrl.toImageUrl()
                                    )
                                )
                            }

                            is ApiResponse.Error -> {
                                Logger.withTag("updateEpisodeArtWork").e("$response")
                            }
                        }
                    }
                }

                Either.Right(Unit)
            }
            .catch { Either.Left(DefaultError(it)) }

}

fun String?.toImageUrl() = FormatterUtil.formatPosterPath(this)
