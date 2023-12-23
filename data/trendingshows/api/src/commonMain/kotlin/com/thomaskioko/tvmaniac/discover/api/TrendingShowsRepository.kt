package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.shows.api.DEFAULT_DAY_TIME_WINDOW
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface TrendingShowsRepository {

    suspend fun fetchTrendingShows(
        timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    ): List<TrendingShows>

    fun observeTrendingShows(
        timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    ): Flow<Either<Failure, List<TrendingShows>>>
}
