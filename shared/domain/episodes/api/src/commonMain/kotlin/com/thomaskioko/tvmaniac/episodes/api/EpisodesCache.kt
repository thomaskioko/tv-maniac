package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import kotlinx.coroutines.flow.Flow
import com.thomaskioko.tvmaniac.datasource.cache.Episode as EpisodeCache

interface EpisodesCache {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun observeEpisode(seasonId: Int): Flow<List<EpisodesBySeasonId>>
}
