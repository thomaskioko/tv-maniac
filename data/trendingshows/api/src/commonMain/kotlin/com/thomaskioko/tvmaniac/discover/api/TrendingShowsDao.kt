package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TrendingShowsDao {
    public fun upsert(show: Trending_shows)

    public fun observeTrendingShows(page: Long): Flow<List<ShowEntity>>

    public fun getPagedTrendingShows(): PagingSource<Int, ShowEntity>

    public fun pageExists(page: Long): Boolean

    public fun deleteTrendingShow(id: Long)

    public fun deleteTrendingShows()
}
