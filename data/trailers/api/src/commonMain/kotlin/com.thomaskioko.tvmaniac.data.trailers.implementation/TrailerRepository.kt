package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
  fun isYoutubePlayerInstalled(): Flow<Boolean>

  fun observeTrailers(id: Long): Flow<Either<Failure, List<Trailers>>>
}
