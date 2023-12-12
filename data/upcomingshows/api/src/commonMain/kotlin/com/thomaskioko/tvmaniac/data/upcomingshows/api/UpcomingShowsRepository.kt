package com.thomaskioko.tvmaniac.data.upcomingshows.api

import com.thomaskioko.tvmaniac.core.db.UpcomingShows
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsRepository {
    suspend fun fetchUpcomingShows(): List<UpcomingShows>
    fun observeUpcomingShows(): Flow<Either<Failure, List<UpcomingShows>>>
}
