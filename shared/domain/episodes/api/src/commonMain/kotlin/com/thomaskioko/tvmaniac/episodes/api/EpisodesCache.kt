package com.thomaskioko.tvmaniac.episodes.api

import kotlinx.coroutines.flow.Flow
import com.thomaskioko.tvmaniac.core.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

interface EpisodesCache {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun observeEpisode(seasonId: Long): Flow<List<EpisodesBySeasonId>>
}
