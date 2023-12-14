package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import kotlinx.coroutines.flow.Flow

interface SeasonsDao {

    fun upsert(season: Season)

    fun upsert(entityList: List<Season>)

    fun fetchSeasonDetails(id: Long): List<SeasonEpisodeDetailsById>

    fun fetchShowSeasons(id: Long): List<ShowSeasons>

    fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>>

    fun observeSeasonEpisodeDetailsById(showId: Long): Flow<List<SeasonEpisodeDetailsById>>

    fun delete(id: Long)

    fun deleteAll()
}
