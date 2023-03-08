package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import kotlinx.coroutines.flow.Flow

interface SeasonsCache {

    fun insertSeason(season: Season)

    fun insertSeasons(entityList: List<Season>)

    fun observeSeasons(traktId: Long): Flow<List<Season>>

    fun insert(entity: Season_episodes)

    fun observeShowEpisodes(showId: Long): Flow<List<SelectSeasonWithEpisodes>>
}
