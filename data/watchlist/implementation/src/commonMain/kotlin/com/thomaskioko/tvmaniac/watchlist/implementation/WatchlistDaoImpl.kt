package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SelectWatchlist
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

    override fun insert(followedShow: Watchlist) {
        database.transaction {
            database.watchlistQueries.insertOrReplace(
                id = followedShow.id,
                synced = followedShow.synced,
                created_at = followedShow.created_at,
            )
        }
    }

    override fun insert(followedShows: List<Watchlist>) {
        followedShows.forEach { insert(it) }
    }

    override fun getWatchlist(): List<SelectWatchlist> =
        database.watchlistQueries.selectWatchlist()
            .executeAsList()

    override fun getUnSyncedShows(): List<Watchlist> =
        database.watchlistQueries.selectUnsyncedShows()
            .executeAsList()

    override fun observeWatchlist(): Flow<List<SelectWatchlist>> =
        database.watchlistQueries.selectWatchlist()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun updateShowSyncState(traktId: Long) {
        database.watchlistQueries.updateFollowedState(
            id = traktId,
            synced = true,
        )
    }

    override fun removeShow(traktId: Long) {
        database.watchlistQueries.removeShow(traktId)
    }
}
