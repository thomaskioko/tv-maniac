package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsDao {
    fun upsert(season: Season)
    fun fetchSeasonDetails(showId: Long, seasonNumber: Long): SeasonDetailsWithEpisodes
    fun observeSeasonEpisodeDetails(showId: Long, seasonNumber: Long): Flow<SeasonDetailsWithEpisodes>
    fun delete(id: Long)
    fun deleteAll()
}
