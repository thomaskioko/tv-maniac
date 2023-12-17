package com.thomaskioko.tvmaniac.data.cast.api

import com.thomaskioko.tvmaniac.core.db.Season_cast
import kotlinx.coroutines.flow.Flow

interface CastRepository {
    fun fetchSeasonCast(seasonId: Long): List<Season_cast>
    fun observeSeasonCast(seasonId: Long): Flow<List<Season_cast>>
}
