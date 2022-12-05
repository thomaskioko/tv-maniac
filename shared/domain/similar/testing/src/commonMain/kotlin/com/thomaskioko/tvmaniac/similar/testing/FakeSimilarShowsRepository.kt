package com.thomaskioko.tvmaniac.similar.testing

import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeSimilarShowsRepository : SimilarShowsRepository {

    private var similarShowsResult: Flow<Resource<List<SelectSimilarShows>>> =
        flowOf(Resource.Success(data = null))

    suspend fun setSimilarShowsResult(result: Resource<List<SelectSimilarShows>>) {
        similarShowsResult = flow { emit(result) }
    }

    override fun observeSimilarShows(traktId: Int): Flow<Resource<List<SelectSimilarShows>>> =
        similarShowsResult
}