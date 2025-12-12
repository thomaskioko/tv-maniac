package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.db.ShowSeasons
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {
    fun observeSeasonsByShowId(id: Long, includeSpecials: Boolean = false): Flow<List<ShowSeasons>>
}
