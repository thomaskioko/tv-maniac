package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {
    fun updateShowArtWork(): Flow<Either<Failure, Unit>>
}
