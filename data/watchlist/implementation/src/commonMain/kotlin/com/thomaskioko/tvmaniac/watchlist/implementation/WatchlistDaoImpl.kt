package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

    override fun upsert(watchlist: Watchlist) {
        database.transaction {
            database.watchlistQueries.insertOrReplace(
                id = watchlist.id,
                synced = watchlist.synced,
                created_at = watchlist.created_at,
            )
        }
    }

    override fun upsert(watchedShowList: List<Watchlist>) {
        watchedShowList.forEach { upsert(it) }
    }

    override fun getWatchedShows(): List<WatchedShow> =
        database.watchlistQueries.watchedShow()
            .executeAsList()

    override fun observeWatchedShows(): Flow<List<WatchedShow>> =
        database.watchlistQueries.watchedShow()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun getUnSyncedShows(): List<Watchlist> =
        database.watchlistQueries.unsyncedShows()
            .executeAsList()

    override fun updateShowSyncState(traktId: Long) {
        database.watchlistQueries.updateFollowedState(
            id = Id(traktId),
            synced = true,
        )
    }

    override fun removeShow(traktId: Long) {
        database.watchlistQueries.removeShowFromWatchlist(Id(traktId))
    }
}
