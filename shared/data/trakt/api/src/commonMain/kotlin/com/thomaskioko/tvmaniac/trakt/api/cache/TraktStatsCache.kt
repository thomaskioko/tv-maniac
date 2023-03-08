package com.thomaskioko.tvmaniac.trakt.api.cache

import com.thomaskioko.tvmaniac.core.db.TraktStats
import kotlinx.coroutines.flow.Flow

interface TraktStatsCache {

    fun insert(stats: TraktStats)

    fun observeStats(): Flow<TraktStats>

}