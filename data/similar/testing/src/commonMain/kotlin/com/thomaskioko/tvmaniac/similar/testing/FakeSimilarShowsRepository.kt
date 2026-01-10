package com.thomaskioko.tvmaniac.similar.testing

import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeSimilarShowsRepository : SimilarShowsRepository {

    private val similarShows = MutableStateFlow<List<SimilarShows>>(emptyList())

    public suspend fun setSimilarShowsResult(result: List<SimilarShows>) {
        similarShows.emit(result)
    }

    override suspend fun fetchSimilarShows(traktId: Long, forceRefresh: Boolean) {
    }

    override fun observeSimilarShows(id: Long): Flow<List<SimilarShows>> = similarShows.asStateFlow()
}
