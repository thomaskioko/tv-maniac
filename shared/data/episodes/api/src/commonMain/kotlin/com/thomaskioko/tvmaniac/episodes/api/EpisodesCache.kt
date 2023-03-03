package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache
import com.thomaskioko.tvmaniac.core.db.EpisodeArt
import kotlinx.coroutines.flow.Flow

interface EpisodesCache {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun observeEpisodeArtByShowId(): Flow<List<EpisodeArt>>
}
