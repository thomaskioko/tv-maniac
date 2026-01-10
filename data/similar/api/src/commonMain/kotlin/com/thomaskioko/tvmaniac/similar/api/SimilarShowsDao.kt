package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.db.SimilarShows
import kotlinx.coroutines.flow.Flow

public interface SimilarShowsDao {

    public fun upsert(showTraktId: Long, showTmdbId: Long, similarShowTraktId: Long, pageOrder: Int = 0)

    public fun observeSimilarShows(showTraktId: Long): Flow<List<SimilarShows>>

    public fun delete(id: Long)

    public fun deleteAll()
}
