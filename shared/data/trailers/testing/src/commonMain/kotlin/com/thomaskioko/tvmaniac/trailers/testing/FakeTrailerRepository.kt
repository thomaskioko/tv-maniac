package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeTrailerRepository : TrailerRepository {
    private var trailersResult: Flow<Either<Failure, List<Trailers>>> = flowOf()

    suspend fun setTrailerResult(result: Either<Failure, List<Trailers>>) {
        trailersResult = flow { emit(result) }
    }

    override fun isWebViewInstalled(): Flow<Boolean> = flowOf()

    override fun observeTrailersByShowId(traktId: Long): Flow<Either<Failure, List<Trailers>>> =
        trailersResult
}