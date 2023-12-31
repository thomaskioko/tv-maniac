package com.thomaskioko.tvmaniac.data.upcomingshows.api

import app.cash.paging.PagingSource
import com.thomaskioko.tvmaniac.core.db.Upcoming_shows
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsDao {
    fun upsert(show: Upcoming_shows)
    fun observeUpcomingShows(): Flow<List<ShowEntity>>
    fun getPagedUpcomingShows(): PagingSource<Int, ShowEntity>
    fun getLastPage(): Long?
    fun deleteUpcomingShow(id: Long)
    fun deleteUpcomingShows()
}
