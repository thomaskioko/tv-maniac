package com.thomaskioko.tvmaniac.discover.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.core.db.Trending_shows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultTrendingShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TrendingShowsDao {

    private val trendingShowsQueries = database.trending_showsQueries

    override fun upsert(show: Trending_shows) {
        trendingShowsQueries.transaction {
            trendingShowsQueries.insert(
                id = show.id,
                page = show.page,
            )
        }
    }

    override fun upsert(list: List<Trending_shows>) {
        list.forEach { upsert(it) }
    }

    override fun observeTvShow(): Flow<List<TrendingShows>> =
        trendingShowsQueries.trendingShows()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteTrendingShow(id: Long) {
        trendingShowsQueries.delete(Id(id))
    }

    override fun deleteTrendingShows() {
        trendingShowsQueries.transaction {
            trendingShowsQueries.deleteAll()
        }
    }
}
