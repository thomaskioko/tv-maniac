package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.Stats
import com.thomaskioko.tvmaniac.profilestats.api.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeStatsRepository : StatsRepository {
    override fun observeStats(slug: String): Flow<StoreReadResponse<Stats>> =
        flowOf()
}
