package com.thomaskioko.tvmaniac.data.recommendedshows.api

import com.thomaskioko.tvmaniac.db.RecommendedShows
import kotlinx.coroutines.flow.Flow

public interface RecommendedShowsDao {
    public fun upsert(showId: Long, recommendedShowId: Long)

    public fun observeRecommendedShows(traktId: Long): Flow<List<RecommendedShows>>

    public fun delete(id: Long)

    public fun deleteAll()
}
