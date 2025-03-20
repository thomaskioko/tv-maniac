package com.thomaskioko.tvmaniac.data.recommendedshows.api

import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface RecommendedShowsRepository {
  fun observeRecommendedShows(
    id: Long,
    forceReload: Boolean = false
  ): Flow<Either<Failure, List<RecommendedShows>>>
}
