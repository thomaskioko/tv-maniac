package com.thomaskioko.tvmaniac.datasource.cache.episode

import com.thomaskioko.tvmaniac.datasource.cache.Episode
import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId

interface EpisodesCache {

    fun insert(entity: com.thomaskioko.tvmaniac.presentation.model.Episode)

    fun insert(list: List<com.thomaskioko.tvmaniac.presentation.model.Episode>)

    fun getEpisodeByEpisodeId(episodeId: Int): Episode

    fun getEpisodesBySeasonId(seasonId: Int): List<EpisodesBySeasonId>
}