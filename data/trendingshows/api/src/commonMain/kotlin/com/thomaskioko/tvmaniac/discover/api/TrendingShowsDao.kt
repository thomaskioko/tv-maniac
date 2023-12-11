package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.core.db.Trending_shows
import kotlinx.coroutines.flow.Flow

interface TrendingShowsDao {
    fun upsert(show: Trending_shows)
    fun upsert(list: List<Trending_shows>)
    fun observeTvShow(): Flow<List<TrendingShows>>
    fun deleteTrendingShow(id: Long)
    fun deleteTrendingShows()
}
