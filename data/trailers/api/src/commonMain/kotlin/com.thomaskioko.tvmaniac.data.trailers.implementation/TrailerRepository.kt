package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.Trailers
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
  fun isYoutubePlayerInstalled(): Flow<Boolean>

  fun observeTrailers(id: Long): Flow<List<Trailers>>
}
