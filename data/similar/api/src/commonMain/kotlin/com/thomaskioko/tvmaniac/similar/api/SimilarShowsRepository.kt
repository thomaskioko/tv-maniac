package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.db.SimilarShows
import kotlinx.coroutines.flow.Flow

public interface SimilarShowsRepository {
    public suspend fun fetchSimilarShows(
        traktId: Long,
        forceRefresh: Boolean = false,
    )

    public fun observeSimilarShows(id: Long): Flow<List<SimilarShows>>
}
