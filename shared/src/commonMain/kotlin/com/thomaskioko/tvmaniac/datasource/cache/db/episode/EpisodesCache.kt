package com.thomaskioko.tvmaniac.datasource.cache.db.episode

import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity

interface EpisodesCache {

    fun insert(entity: EpisodeEntity)

    fun insert(entityList: List<EpisodeEntity>)

    fun getEpisodeByEpisodeId(episodeId: Int): EpisodeEntity

    fun getEpisodesBySeasonId(seasonId: Int): List<EpisodeEntity>
}