package com.thomaskioko.tvmaniac.trakt.profile.api.cache

import com.thomaskioko.tvmaniac.core.db.User_stats
import kotlinx.coroutines.flow.Flow

interface StatsCache {

    fun insert(stats: User_stats)

    fun observeStats(): Flow<User_stats>
}
