package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import kotlinx.coroutines.flow.Flow

interface SeasonsDao {

    fun upsert(season: Season)

    fun upsert(entityList: List<Season>)

    fun fetchSeasonDetails(traktId: Long): List<SeasonEpisodeDetailsById>

    fun observeSeasonsByShowId(traktId: Long): Flow<List<SeasonsByShowId>>

    fun observeSeasonEpisodeDetailsById(showId: Long): Flow<List<SeasonEpisodeDetailsById>>

    fun delete(id: Long)

    fun deleteAll()
}
