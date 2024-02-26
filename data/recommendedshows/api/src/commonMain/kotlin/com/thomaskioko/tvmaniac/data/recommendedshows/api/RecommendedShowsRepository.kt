package com.thomaskioko.tvmaniac.data.recommendedshows.api

import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface RecommendedShowsRepository {
  suspend fun fetchRecommendedShows(id: Long): List<RecommendedShows>

  fun observeRecommendedShows(id: Long): Flow<Either<Failure, List<RecommendedShows>>>
}
