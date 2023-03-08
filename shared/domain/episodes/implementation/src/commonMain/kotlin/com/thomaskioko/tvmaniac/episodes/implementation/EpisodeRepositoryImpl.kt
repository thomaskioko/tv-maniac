package com.thomaskioko.tvmaniac.episodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.EpisodesByShowId
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EpisodeRepositoryImpl(
    private val tmdbService: TmdbService,
    private val traktService: TraktService,
    private val episodesCache: EpisodesCache,
    private val episodeImageCache: EpisodeImageCache,
    private val dispatcher: CoroutineDispatcher,
) : EpisodeRepository {

    override fun observeSeasonEpisodes(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ): Flow<Resource<List<EpisodesByShowId>>> = networkBoundResource(
        query = { episodesCache.observeEpisodesByShowId(seasonId) },
        shouldFetch = { it.isNullOrEmpty() },
        fetch = { traktService.getSeasonWithEpisodes(tvShowId) },
        saveFetchResult = { episodesCache.insert(it.toEpisodeCacheList()) },
        onFetchFailed = { Logger.withTag("observeSeasonEpisodes").e(it.resolveError()) },
        coroutineDispatcher = dispatcher
    )

    override fun observeSeasonEpisodes(seasonId: Int): Flow<List<EpisodesByShowId>> =
        episodesCache.observeEpisodesByShowId(seasonId)

    override fun observeUpdateEpisodeArtWork(showId: Int): Flow<Unit> =
        episodesCache.observeEpisodeArtByShowId(showId)
            .map { episodes ->

                episodes.forEach { episode ->
                    episode.tmdb_id_?.let {
                        val response = tmdbService.getEpisodeDetails(
                            tmdbShow = episode.tmdb_id_!!,
                            ssnNumber = episode.season_number!!,
                            epNumber = episode.episode_number.toInt()
                        )

                        episodeImageCache.insert(
                            EpisodeImage(
                                id = episode.id,
                                image_url = response.still_path
                            )
                        )

                    }
                }

            }
}
