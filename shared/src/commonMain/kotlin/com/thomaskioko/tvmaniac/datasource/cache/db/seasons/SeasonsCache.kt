package com.thomaskioko.tvmaniac.datasource.cache.db.seasons

import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity

interface SeasonsCache {

    fun insert(entity: SeasonsEntity)

    fun insert(entityList: List<SeasonsEntity>)

    fun getSeasonBySeasonId(seasonId: Int): SeasonsEntity

    fun getSeasonsByTvShowId(tvShowId: Int): List<SeasonsEntity>

    fun updateSeasonEpisodes(entity: SeasonsEntity)

}