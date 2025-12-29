package com.thomaskioko.tvmaniac.data.recommendedshows.api

import com.thomaskioko.tvmaniac.db.RecommendedShows
import kotlinx.coroutines.flow.Flow

public interface RecommendedShowsRepository {
    public suspend fun fetchRecommendedShows(
        id: Long,
        forceRefresh: Boolean = false,
    )

    public fun observeRecommendedShows(
        id: Long,
    ): Flow<List<RecommendedShows>>
}
