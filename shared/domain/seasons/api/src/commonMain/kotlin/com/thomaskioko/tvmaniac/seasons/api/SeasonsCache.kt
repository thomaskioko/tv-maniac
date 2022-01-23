package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import kotlinx.coroutines.flow.Flow

interface SeasonsCache {

    fun insert(tvSeason: Season)

    fun insert(entityList: List<Season>)

    fun getSeasonBySeasonId(seasonId: Long): Season

    fun observeSeasons(tvShowId: Long): Flow<List<SelectSeasonsByShowId>>

    fun updateSeasonEpisodesIds(seasonId: Long, episodeIds: List<Int>)
}
