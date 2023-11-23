package com.thomaskioko.tvmaniac.profilestats.api

import com.thomaskioko.tvmaniac.core.db.Stats
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun observeStats(slug: String): Flow<Either<Failure, Stats>>
}
