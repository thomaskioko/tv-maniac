package com.thomaskioko.tvmaniac.profilestats.api

import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun observeStats(slug: String, refresh: Boolean = false): Flow<Either<Failure, User_stats>>
}
