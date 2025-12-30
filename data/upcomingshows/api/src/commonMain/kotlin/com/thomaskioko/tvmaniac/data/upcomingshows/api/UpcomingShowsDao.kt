package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.Upcoming_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface UpcomingShowsDao {
    public fun upsert(show: Upcoming_shows)

    public fun observeUpcomingShows(page: Long): Flow<List<ShowEntity>>

    public fun getPagedUpcomingShows(): PagingSource<Int, ShowEntity>

    public fun pageExists(page: Long): Boolean

    public fun deleteUpcomingShow(id: Long)

    public fun deleteUpcomingShows()
}
