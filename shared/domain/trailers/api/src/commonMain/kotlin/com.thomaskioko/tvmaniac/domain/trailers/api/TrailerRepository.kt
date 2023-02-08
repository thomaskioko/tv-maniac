package com.thomaskioko.tvmaniac.domain.trailers.api

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
    fun isWebViewInstalled(): Flow<Boolean>
    fun observeTrailersByShowId(traktId: Long): Flow<Either<Failure, List<Trailers>>>
}