package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

// TODO:: Use selector in settings to determine time window
const val DEFAULT_DAY_TIME_WINDOW: String = "day"
const val DEFAULT_WEEK_TIME_WINDOW: String = "week"

interface TrendingShowsRepository {

    fun observeTrendingShows(
        timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    ): Flow<Either<Failure, List<TrendingShows>>>

    fun observeFeaturedTrendingShows(
        timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    ): Flow<Either<Failure, List<TrendingShows>>>

    suspend fun fetchTrendingShows(
        timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    ): List<TrendingShows>

    suspend fun fetchFeaturedTrendingShows(
        timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    ): List<TrendingShows>
}
