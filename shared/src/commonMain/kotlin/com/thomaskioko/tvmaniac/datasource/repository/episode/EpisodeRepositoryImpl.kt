package com.thomaskioko.tvmaniac.datasource.repository.episode

import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeCacheList
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeEntityList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.presentation.model.Episode

class EpisodeRepositoryImpl(
    private val apiService: TvShowsService,
    private val episodesCache: EpisodesCache,
    private val seasonCache: SeasonsCache
) : EpisodeRepository {

    override suspend fun getEpisodeByEpisodeId(episodeId: Int): Episode {
        return episodesCache.getEpisodeByEpisodeId(episodeId)
            .toEpisodeEntity()
    }

    override suspend fun getEpisodesBySeasonId(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ): List<Episode> {
        return if (episodesCache.getEpisodesBySeasonId(seasonId).isEmpty()) {
            fetchAndUpdateSeasonEpisodes(tvShowId, seasonId, seasonNumber)

            episodesCache.getEpisodesBySeasonId(seasonId)
                .toEpisodeEntityList()
        } else {
            episodesCache.getEpisodesBySeasonId(seasonId)
                .toEpisodeEntityList()
        }
    }

    override suspend fun fetchAndUpdateSeasonEpisodes(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ) {
        val episodeEntityList = apiService.getSeasonDetails(tvShowId, seasonNumber)
            .toEpisodeCacheList()

        //Insert episodes
        episodesCache.insert(episodeEntityList)

        val episodesIds = mutableListOf<Int>()
        for (episode in episodeEntityList) {
            episodesIds.add(episode.id.toInt())
        }

        //Update season episode list
        seasonCache.updateSeasonEpisodesIds(seasonId =  seasonId, episodeIds = episodesIds)
    }
}