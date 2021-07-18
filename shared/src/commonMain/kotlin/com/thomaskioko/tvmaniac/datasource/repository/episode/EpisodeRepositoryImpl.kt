package com.thomaskioko.tvmaniac.datasource.repository.episode

import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeEntityList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService

class EpisodeRepositoryImpl(
    private val apiService: TvShowsService,
    private val episodesCache: EpisodesCache,
    private val seasonCache: SeasonsCache
) : EpisodeRepository {

    override suspend fun getEpisodeByEpisodeId(episodeId: Int): EpisodeEntity {
        return episodesCache.getEpisodeByEpisodeId(episodeId)
    }

    override suspend fun getEpisodesBySeasonId(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ): List<EpisodeEntity> {
        return if (episodesCache.getEpisodesBySeasonId(seasonId).isEmpty()) {
            fetchAndUpdateSeasonEpisodes(tvShowId, seasonId, seasonNumber)

            episodesCache.getEpisodesBySeasonId(seasonId)
        } else {
            episodesCache.getEpisodesBySeasonId(seasonId)
        }
    }

    override suspend fun fetchAndUpdateSeasonEpisodes(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ) {
        val episodeEntityList = apiService.getSeasonDetails(tvShowId, seasonNumber)
            .toEpisodeEntityList()

        //Insert episodes
        episodesCache.insert(episodeEntityList)

        val seasonCacheResult = seasonCache.getSeasonBySeasonId(seasonId)

        //Update season episode list
        seasonCache.updateSeasonEpisodes(
            seasonCacheResult.copy(
                episodeList = episodeEntityList
            )
        )
    }
}