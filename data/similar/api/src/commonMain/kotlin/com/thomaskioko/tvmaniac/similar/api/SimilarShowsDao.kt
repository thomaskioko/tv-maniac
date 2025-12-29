package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.db.SimilarShows
import kotlinx.coroutines.flow.Flow

public interface SimilarShowsDao {

    public fun upsert(showId: Long, similarShowId: Long, pageOrder: Int = 0)

    public fun observeSimilarShows(showId: Long): Flow<List<SimilarShows>>

    public fun delete(id: Long)

    public fun deleteAll()
}
