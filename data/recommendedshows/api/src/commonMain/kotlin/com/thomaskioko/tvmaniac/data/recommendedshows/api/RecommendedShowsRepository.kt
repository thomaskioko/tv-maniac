package com.thomaskioko.tvmaniac.data.recommendedshows.api

import com.thomaskioko.tvmaniac.db.RecommendedShows
import kotlinx.coroutines.flow.Flow

interface RecommendedShowsRepository {
  suspend fun fetchRecommendedShows(
    id: Long,
    forceRefresh: Boolean = false
  )

  fun observeRecommendedShows(
    id: Long,
  ): Flow<List<RecommendedShows>>
}
