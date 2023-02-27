package com.thomaskioko.tvmaniac.episodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService

class EpisodeRepositoryImpl(
    private val tmdbService: TmdbService,
    private val episodesCache: EpisodesCache,
    private val episodeImageCache: EpisodeImageCache,
) : EpisodeRepository {

    override suspend fun updateEpisodeArtWork(showId: Long) {
        episodesCache.observeEpisodeArtByShowId(showId)
            .map { episode ->

                episode.tmdb_id?.let { tmdbId ->
                    val response = tmdbService.getEpisodeDetails(
                        tmdbShow = tmdbId,
                        ssnNumber = episode.season_number!!,
                        epNumber = episode.episode_number.toLong()
                    )

                    when (response) {
                        is ApiResponse.Success -> {
                            episodeImageCache.insert(
                                EpisodeImage(
                                    trakt_id = episode.id,
                                    tmdb_id = response.body.id.toLong(),
                                    image_url = response.body.still_path
                                )
                            )
                        }

                        is ApiResponse.Error -> {
                            Logger.withTag("updateEpisodeArtWork").e("$response")
                        }
                    }
                }
            }
    }
}
