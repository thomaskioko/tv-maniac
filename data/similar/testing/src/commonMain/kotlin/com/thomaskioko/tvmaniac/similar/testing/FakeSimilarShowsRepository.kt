package com.thomaskioko.tvmaniac.similar.testing

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeSimilarShowsRepository : SimilarShowsRepository {

    private var similarShowsResult: Flow<StoreReadResponse<List<SimilarShows>>> = flowOf()

    suspend fun setSimilarShowsResult(result: StoreReadResponse<List<SimilarShows>>) {
        similarShowsResult = flow { emit(result) }
    }

    override fun observeSimilarShows(traktId: Long): Flow<StoreReadResponse<List<SimilarShows>>> =
        similarShowsResult
}
