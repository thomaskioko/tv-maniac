package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowSeasons
import kotlinx.coroutines.flow.Flow

interface SeasonsDao {

    fun upsert(season: Season)

    fun upsert(entityList: List<Season>)

    fun fetchShowSeasons(id: Long, includeSpecials: Boolean = true): List<ShowSeasons>

    fun observeSeasonsByShowId(id: Long, includeSpecials: Boolean = true): Flow<List<ShowSeasons>>

    fun delete(id: Long)

    fun deleteAll()
}
