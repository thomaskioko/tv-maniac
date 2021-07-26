package com.thomaskioko.tvmaniac.datasource.repository.episode

import com.thomaskioko.tvmaniac.presentation.model.Episode

interface EpisodeRepository {

    suspend fun getEpisodeByEpisodeId(episodeId: Int): Episode

    suspend fun getEpisodesBySeasonId(tvShowId :Int, seasonId: Int, seasonNumber: Int): List<Episode>

    suspend fun fetchAndUpdateSeasonEpisodes(tvShowId :Int, seasonId: Int, seasonNumber: Int)
}