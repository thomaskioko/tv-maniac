package com.thomaskioko.tvmaniac.toprated.data.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.PagedTopRatedShows
import com.thomaskioko.tvmaniac.core.db.TopRatedShows
import com.thomaskioko.tvmaniac.core.db.Toprated_shows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultTopRatedShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TopRatedShowsDao {
    private val topRatedShowsQueries = database.toprated_showsQueries

    override fun upsert(show: Toprated_shows) {
        topRatedShowsQueries.transaction {
            topRatedShowsQueries.insert(
                id = show.id,
                page = show.page,
            )
        }
    }

    override fun upsert(list: List<Toprated_shows>) {
        list.forEach { upsert(it) }
    }

    override fun observeTrendingShows(): Flow<List<TopRatedShows>> =
        topRatedShowsQueries.topRatedShows()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeTrendingShows(page: Long): Flow<List<PagedTopRatedShows>> =
        topRatedShowsQueries.pagedTopRatedShows(Id(page))
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteTrendingShows(id: Long) {
        topRatedShowsQueries.delete(Id(id))
    }

    override fun deleteTrendingShows() {
        topRatedShowsQueries.transaction {
            topRatedShowsQueries.deleteAll()
        }
    }
}
