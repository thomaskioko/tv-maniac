package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.EpisodeArt
import kotlinx.coroutines.flow.Flow
import com.thomaskioko.tvmaniac.core.db.Episodes as EpisodeCache

interface EpisodesDao {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun observeEpisodeArtByShowId(): Flow<List<EpisodeArt>>

    fun delete(id: Long)

    fun deleteAll()
}
