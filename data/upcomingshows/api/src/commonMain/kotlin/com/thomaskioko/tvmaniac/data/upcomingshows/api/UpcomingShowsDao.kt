package com.thomaskioko.tvmaniac.data.upcomingshows.api

import com.thomaskioko.tvmaniac.core.db.PagedUpcomingShows
import com.thomaskioko.tvmaniac.core.db.UpcomingShows
import com.thomaskioko.tvmaniac.core.db.Upcoming_shows
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsDao {
    fun upsert(show: Upcoming_shows)
    fun upsert(list: List<Upcoming_shows>)
    fun observeUpcomingShows(): Flow<List<UpcomingShows>>
    fun observeUpcomingShows(page: Long): Flow<List<PagedUpcomingShows>>
    fun deleteUpcomingShow(id: Long)
    fun deleteUpcomingShows()
}
