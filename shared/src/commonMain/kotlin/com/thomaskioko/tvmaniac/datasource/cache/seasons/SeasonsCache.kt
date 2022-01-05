package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import kotlinx.coroutines.flow.Flow

interface SeasonsCache {

    fun insert(tvSeason: Season)

    fun insert(entityList: List<Season>)

    fun getSeasonBySeasonId(seasonId: Int): Season

    fun observeSeasons(tvShowId: Int): Flow<List<SelectSeasonsByShowId>>

    fun updateSeasonEpisodesIds(seasonId: Int, episodeIds: List<Int>)
}
