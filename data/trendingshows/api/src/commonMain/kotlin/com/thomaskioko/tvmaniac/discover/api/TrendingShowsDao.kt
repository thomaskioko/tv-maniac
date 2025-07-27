package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TrendingShowsDao {
    fun upsert(show: Trending_shows)

    fun observeTrendingShows(page: Long): Flow<List<ShowEntity>>

    fun getPagedTrendingShows(): PagingSource<Int, ShowEntity>

    fun pageExists(page: Long): Boolean

    fun deleteTrendingShow(id: Long)

    fun deleteTrendingShows()
}
