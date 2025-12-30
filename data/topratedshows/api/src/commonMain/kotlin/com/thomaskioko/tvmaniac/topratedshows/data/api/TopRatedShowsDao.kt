package com.thomaskioko.tvmaniac.topratedshows.data.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.TopRatedShows
import com.thomaskioko.tvmaniac.db.Toprated_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TopRatedShowsDao {
    public fun upsert(show: Toprated_shows)

    public fun observeTopRatedShows(): Flow<List<TopRatedShows>>

    public fun getPagedTopRatedShows(): PagingSource<Int, ShowEntity>

    public fun observeTopRatedShows(page: Long): Flow<List<ShowEntity>>

    public fun pageExists(page: Long): Boolean

    public fun deleteTrendingShows(id: Long)

    public fun deleteTrendingShows()
}
