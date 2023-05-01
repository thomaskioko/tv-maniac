package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {
    fun updateShowArtWork(): Flow<Either<Failure, Unit>>
}
