package com.thomaskioko.tvmaniac.tmdb.testing

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeTmdbRepository : TmdbRepository {

    override fun updateShowArtWork(): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))
}
