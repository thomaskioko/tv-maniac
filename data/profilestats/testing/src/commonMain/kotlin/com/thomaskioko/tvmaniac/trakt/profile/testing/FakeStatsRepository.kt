package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.Stats
import com.thomaskioko.tvmaniac.profilestats.api.StatsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeStatsRepository : StatsRepository {
    override fun observeStats(slug: String): Flow<Either<Failure, Stats>> =
        flowOf()
}
