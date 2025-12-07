package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.db.SimilarShows
import kotlinx.coroutines.flow.Flow

interface SimilarShowsDao {

    fun upsert(showId: Long, similarShowId: Long, pageOrder: Int = 0)

    fun observeSimilarShows(showId: Long): Flow<List<SimilarShows>>

    fun delete(id: Long)

    fun deleteAll()
}
