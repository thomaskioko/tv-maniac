package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.Upcoming_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsDao {
    fun upsert(show: Upcoming_shows)

    fun observeUpcomingShows(page: Long): Flow<List<ShowEntity>>

    fun getPagedUpcomingShows(): PagingSource<Int, ShowEntity>

    fun pageExists(page: Long): Boolean

    fun deleteUpcomingShow(id: Long)

    fun deleteUpcomingShows()
}
