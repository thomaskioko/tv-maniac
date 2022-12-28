package com.thomaskioko.tvmaniac.similar.testing

import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeSimilarShowsRepository : SimilarShowsRepository {

    private var similarShowsResult: Flow<Either<Failure, List<SelectSimilarShows>>> =
        flowOf(Either.Right(data = null))

    suspend fun setSimilarShowsResult(result: Either<Failure, List<SelectSimilarShows>>) {
        similarShowsResult = flow { emit(result) }
    }

    override fun observeSimilarShows(traktId: Int): Flow<Either<Failure, List<SelectSimilarShows>>> =
        similarShowsResult
}