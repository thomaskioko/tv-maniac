package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonEpisodesRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SeasonEpisodesRepositoryImpl(
    private val tmdbService: TmdbService,
    private val traktService: TraktService,
    private val tvShowCache: TvShowCache,
    private val episodesCache: EpisodesCache,
    private val seasonsCache: SeasonsCache,
    private val seasonWithEpisodesCache: SeasonWithEpisodesCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonEpisodesRepository {

    override fun observeSeasonEpisodes(
        showId: Int,
    ): Flow<Resource<List<SelectSeasonWithEpisodes>>> =
        networkBoundResource(
            query = { seasonWithEpisodesCache.observeShowEpisodes(showId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSeasonWithEpisodes(showId) },
            saveFetchResult = { mapAndCache(showId, it) },
            onFetchFailed = {
                Logger.withTag("observeSeasonEpisodes").e(it) { it.message ?: "Error" }
            },
            coroutineDispatcher = dispatcher
        )

    override fun syncSeasonEpisodeArtWork(traktId: Int): Flow<Unit> =
        seasonsCache.observeSeasons(traktId)
            .map { seasonsList ->
                val show = tvShowCache.getTvShow(seasonsList.first().trakt_id)

                seasonsList.forEach { season ->
                    //Fetch episode poster paths
                    episodesCache.getEpisode(season.trakt_id_!!)
                        .filter { it.image_url == null }
                        .forEach { episode ->

                            //getImage url
                            val episodeResponse = tmdbService.getEpisodeDetails(
                                show!!.tmdb_id!!,
                                episode.season_number!!,
                                episode.episode_number.toInt()
                            )

                            episodesCache.updatePoster(
                                episodeId = episode.tmdb_id!!,
                                posterPath = episodeResponse.still_path
                            )
                        }
                }
            }


    private fun mapAndCache(traktId: Int, response: List<TraktSeasonEpisodesResponse>) {
        response.forEach { season ->
            episodesCache.insert(season.toEpisodeCacheList())

            seasonWithEpisodesCache.insert(
                Season_with_episodes(
                    show_id = traktId,
                    season_id = season.ids.trakt,
                    season_number = season.number,
                )
            )
        }
    }
}
