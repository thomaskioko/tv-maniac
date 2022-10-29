package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.EpisodeArtByShowId
import kotlinx.coroutines.flow.Flow
import com.thomaskioko.tvmaniac.core.db.EpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

interface EpisodesCache {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun observeEpisodesByShowId(id: Int): Flow<List<EpisodesByShowId>>

    fun observeEpisodeArtByShowId(id: Int): Flow<List<EpisodeArtByShowId>>
}
