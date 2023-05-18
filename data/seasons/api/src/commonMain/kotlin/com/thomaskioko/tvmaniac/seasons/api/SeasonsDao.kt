package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.Seasons
import kotlinx.coroutines.flow.Flow

interface SeasonsDao {

    fun insertSeason(season: Seasons)

    fun insertSeasons(entityList: List<Seasons>)

    fun observeSeasons(traktId: Long): Flow<List<Seasons>>

    fun delete(id: Long)

    fun deleteAll()
}
