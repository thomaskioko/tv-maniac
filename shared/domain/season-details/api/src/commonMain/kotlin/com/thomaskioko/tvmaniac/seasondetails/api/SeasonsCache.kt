package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import kotlinx.coroutines.flow.Flow

interface SeasonsCache {

    fun insertSeason(tvSeason: Season)

    fun insertSeasons(entityList: List<Season>)

    fun observeSeasons(traktId: Int): Flow<List<SelectSeasonsByShowId>>

    fun insert(entity: Season_with_episodes)

    fun insert(list: List<Season_with_episodes>)

    fun observeShowEpisodes(showId: Int): Flow<List<SelectSeasonWithEpisodes>>
}
