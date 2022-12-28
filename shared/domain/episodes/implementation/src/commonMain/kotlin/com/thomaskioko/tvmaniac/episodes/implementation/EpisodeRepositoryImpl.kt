package com.thomaskioko.tvmaniac.episodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.EpisodesByShowId
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EpisodeRepositoryImpl(
    private val tmdbService: TmdbService,
    private val episodesCache: EpisodesCache,
    private val episodeImageCache: EpisodeImageCache,
) : EpisodeRepository {

    override fun observeSeasonEpisodes(seasonId: Int): Flow<List<EpisodesByShowId>> =
        episodesCache.observeEpisodesByShowId(seasonId)

    override fun updateEpisodeArtWork(showId: Int): Flow<Unit> =
        episodesCache.observeEpisodeArtByShowId(showId)
            .map { episodes ->

                episodes.forEach { episode ->
                    episode.tmdb_id_?.let {
                        val response = tmdbService.getEpisodeDetails(
                            tmdbShow = episode.tmdb_id_!!,
                            ssnNumber = episode.season_number!!,
                            epNumber = episode.episode_number.toInt()
                        )

                        when (response) {
                            is ApiResponse.Error -> {
                                Logger.withTag("updateEpisodeArtWork")
                                    .e("$response")
                            }

                            is ApiResponse.Success -> {
                                episodeImageCache.insert(
                                    EpisodeImage(
                                        id = episode.id,
                                        image_url = response.body.still_path
                                    )
                                )
                            }
                        }


                    }
                }

            }
}
