package com.thomaskioko.tvmaniac.data.recommendedshows.testing

import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.db.RecommendedShows
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeRecommendedShowsRepository : RecommendedShowsRepository {

    private val entityListResult = MutableStateFlow<List<RecommendedShows>>(emptyList())

    public suspend fun setObserveRecommendedShows(result: List<RecommendedShows>) {
        entityListResult.emit(result)
    }

    override suspend fun fetchRecommendedShows(id: Long, forceRefresh: Boolean) {
    }

    override fun observeRecommendedShows(id: Long): Flow<List<RecommendedShows>> = entityListResult.asStateFlow()
}
