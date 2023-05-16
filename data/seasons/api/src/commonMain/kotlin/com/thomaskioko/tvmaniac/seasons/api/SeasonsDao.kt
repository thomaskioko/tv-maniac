package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import kotlinx.coroutines.flow.Flow

interface SeasonsDao {

    fun insertSeason(season: Seasons)

    fun insertSeasons(entityList: List<Seasons>)

    fun observeSeasons(traktId: Long): Flow<List<Seasons>>

    fun insert(entity: Season_episodes)

    fun observeShowEpisodes(showId: Long): Flow<List<SelectSeasonWithEpisodes>>
}
