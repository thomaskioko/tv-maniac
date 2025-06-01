package com.thomaskioko.tvmaniac.data.recommendedshows.api

import com.thomaskioko.tvmaniac.db.RecommendedShows
import kotlinx.coroutines.flow.Flow

interface RecommendedShowsDao {
    fun upsert(showId: Long, recommendedShowId: Long)

    fun observeRecommendedShows(traktId: Long): Flow<List<RecommendedShows>>

    fun delete(id: Long)

    fun deleteAll()
}
