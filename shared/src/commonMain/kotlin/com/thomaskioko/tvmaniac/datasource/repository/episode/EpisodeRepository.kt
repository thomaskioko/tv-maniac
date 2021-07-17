package com.thomaskioko.tvmaniac.datasource.repository.episode

import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity

interface EpisodeRepository {

    suspend fun getEpisodeByEpisodeId(episodeId: Int): EpisodeEntity

    suspend fun getEpisodesBySeasonId(tvShowId :Int, seasonId: Int, seasonNumber: Int): List<EpisodeEntity>

    suspend fun fetchAndUpdateSeasonEpisodes(tvShowId :Int, seasonId: Int, seasonNumber: Int)
}