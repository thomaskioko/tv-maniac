package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeTrailerRepository : TrailerRepository {
    private var trailersResult: Flow<Either<Failure, List<Trailers>>> =
        flowOf(Either.Right(data = null))

    suspend fun setTrailerResult(result: Either<Failure, List<Trailers>>) {
        trailersResult = flow { emit(result) }
    }

    override fun isWebViewInstalled(): Flow<Boolean> = flowOf(false)

    override fun observeTrailersByShowId(traktId: Int): Flow<Either<Failure, List<Trailers>>> =
        trailersResult
}