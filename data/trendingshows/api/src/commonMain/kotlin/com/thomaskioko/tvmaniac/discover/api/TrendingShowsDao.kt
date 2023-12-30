package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.Trending_shows
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TrendingShowsDao {
    fun upsert(show: Trending_shows)
    fun upsert(list: List<Trending_shows>)
    fun observeTvShow(): Flow<List<ShowEntity>>
    fun deleteTrendingShow(id: Long)
    fun deleteTrendingShows()
}
