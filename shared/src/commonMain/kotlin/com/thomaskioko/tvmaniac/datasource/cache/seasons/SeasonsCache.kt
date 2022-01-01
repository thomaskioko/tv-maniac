package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId

interface SeasonsCache {

    fun insert(tvSeason: Season)

    fun insert(entityList: List<Season>)

    fun getSeasonBySeasonId(seasonId: Int): Season

    fun getSeasonsByTvShowId(tvShowId: Int): List<SelectSeasonsByShowId>

    fun updateSeasonEpisodesIds(seasonId: Int, episodeIds: List<Int>)
}
