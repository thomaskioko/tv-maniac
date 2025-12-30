package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.db.ShowSeasons
import kotlinx.coroutines.flow.Flow

public interface SeasonsRepository {
    public fun observeSeasonsByShowId(id: Long, includeSpecials: Boolean = false): Flow<List<ShowSeasons>>
}
