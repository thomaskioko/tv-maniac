package com.thomaskioko.tvmaniac.data.cast.api

import com.thomaskioko.tvmaniac.core.db.Season_cast
import kotlinx.coroutines.flow.Flow

interface CastDao {
    fun upsert(entity: Season_cast)
    fun fetchSeasonCast(id: Long): List<Season_cast>
    fun observeSeasonCast(id: Long): Flow<List<Season_cast>>
}
