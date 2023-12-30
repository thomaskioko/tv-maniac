package com.thomaskioko.tvmaniac.topratedshows.data.api

import com.thomaskioko.tvmaniac.core.db.TopRatedShows
import com.thomaskioko.tvmaniac.core.db.Toprated_shows
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TopRatedShowsDao {
    fun upsert(show: Toprated_shows)
    fun upsert(list: List<Toprated_shows>)
    fun observeTrendingShows(): Flow<List<TopRatedShows>>
    fun observeTrendingShows(page: Long): Flow<List<ShowEntity>>
    fun deleteTrendingShows(id: Long)
    fun deleteTrendingShows()
}
