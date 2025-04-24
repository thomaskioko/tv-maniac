package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.db.SimilarShows
import kotlinx.coroutines.flow.Flow

interface SimilarShowsRepository {
  suspend fun fetchSimilarShows(
    id: Long,
    forceRefresh: Boolean = false
  )

  fun observeSimilarShows(id: Long): Flow<List<SimilarShows>>
}
