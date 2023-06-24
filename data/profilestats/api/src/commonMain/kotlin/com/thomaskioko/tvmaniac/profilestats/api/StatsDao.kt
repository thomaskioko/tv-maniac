package com.thomaskioko.tvmaniac.profilestats.api

import com.thomaskioko.tvmaniac.core.db.Stats
import kotlinx.coroutines.flow.Flow

interface StatsDao {

    fun insert(stats: Stats)

    fun observeStats(slug: String): Flow<Stats>

    fun delete(slug: String)

    fun deleteAll()
}
