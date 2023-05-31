package com.thomaskioko.tvmaniac.profilestats.api

import com.thomaskioko.tvmaniac.core.db.Stats
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse

interface StatsRepository {
    fun observeStats(slug: String): Flow<StoreReadResponse<Stats>>
}
