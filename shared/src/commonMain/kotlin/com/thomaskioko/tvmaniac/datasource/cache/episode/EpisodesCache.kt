package com.thomaskioko.tvmaniac.datasource.cache.episode

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.Episode as EpisodeCache

interface EpisodesCache {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun getEpisodeByEpisodeId(episodeId: Int): EpisodeCache

    fun getEpisodesBySeasonId(seasonId: Int): List<EpisodesBySeasonId>
}
