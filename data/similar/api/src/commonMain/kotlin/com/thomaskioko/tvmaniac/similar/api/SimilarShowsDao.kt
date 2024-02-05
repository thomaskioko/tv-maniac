package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import kotlinx.coroutines.flow.Flow

interface SimilarShowsDao {

  fun upsert(showId: Long, similarShowId: Long)

  fun observeSimilarShows(traktId: Long): Flow<List<SimilarShows>>

  fun delete(id: Long)

  fun deleteAll()
}
