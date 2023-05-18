package com.thomaskioko.tvmaniac.profilestats.api

import com.thomaskioko.tvmaniac.core.db.User_stats
import kotlinx.coroutines.flow.Flow

interface StatsDao {

    fun insert(stats: User_stats)

    fun observeStats(): Flow<User_stats>
}
