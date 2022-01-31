package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import kotlinx.coroutines.flow.Flow

interface SeasonsCache {

    fun insert(tvSeason: Season)

    fun insert(entityList: List<Season>)

    fun getSeasonBySeasonId(seasonId: Long): Season

    fun getSeasonsByShowId(showId: Long): List<SelectSeasonsByShowId>

    fun observeSeasons(tvShowId: Long): Flow<List<SelectSeasonsByShowId>>
}
