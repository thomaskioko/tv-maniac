package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
  fun isYoutubePlayerInstalled(): Flow<Boolean>

  fun observeTrailersStoreResponse(id: Long): Flow<Either<Failure, List<Trailers>>>

  suspend fun fetchTrailersByShowId(id: Long): List<Trailers>
}
