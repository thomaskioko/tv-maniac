package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
    fun isYoutubePlayerInstalled(): Flow<Boolean>
    fun observeTrailersByShowId(traktId: Long): Flow<Either<Failure, List<Trailers>>>
}
