package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.PagedUpcomingShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.UpcomingShows
import com.thomaskioko.tvmaniac.core.db.Upcoming_shows
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultUpcomingShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : UpcomingShowsDao {
    private val upcomingShowsQueries = database.upcoming_showsQueries

    override fun upsert(show: Upcoming_shows) {
        upcomingShowsQueries.transaction {
            upcomingShowsQueries.insert(
                id = show.id,
                page = show.page,
            )
        }
    }

    override fun upsert(list: List<Upcoming_shows>) {
        list.forEach { upsert(it) }
    }

    override fun observeUpcomingShows(): Flow<List<UpcomingShows>> =
        upcomingShowsQueries.upcomingShows()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeUpcomingShows(page: Long): Flow<List<PagedUpcomingShows>> =
        upcomingShowsQueries.pagedUpcomingShows(Id(page))
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteUpcomingShow(id: Long) {
        upcomingShowsQueries.delete(Id(id))
    }

    override fun deleteUpcomingShows() {
        upcomingShowsQueries.transaction {
            upcomingShowsQueries.deleteAll()
        }
    }
}
