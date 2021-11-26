package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Tv_season

interface SeasonsCache {

    fun insert(tvSeason: Tv_season)

    fun insert(entityList: List<Tv_season>)

    fun getSeasonBySeasonId(seasonId: Int): Tv_season

    fun getSeasonsByTvShowId(tvShowId: Int): List<SelectSeasonsByShowId>

    fun updateSeasonEpisodesIds(seasonId: Int, episodeIds: List<Int>)
}
