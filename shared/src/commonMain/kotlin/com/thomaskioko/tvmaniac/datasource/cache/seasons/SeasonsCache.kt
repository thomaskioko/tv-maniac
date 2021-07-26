package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId

interface SeasonsCache {

    fun insert(entity: com.thomaskioko.tvmaniac.presentation.model.Season)

    fun insert(entityList: List<com.thomaskioko.tvmaniac.presentation.model.Season>)

    fun getSeasonBySeasonId(seasonId: Int): Season

    fun getSeasonsByTvShowId(tvShowId: Int): List<SelectSeasonsByShowId>

    fun updateSeasonEpisodes(entity: com.thomaskioko.tvmaniac.presentation.model.Season)

}