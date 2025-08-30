package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchlistDao(
    private val database: TvManiacDatabase,
    private val dateFormatter: PlatformDateFormatter,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

    override fun upsert(id: Long) {
        database.transaction {
            database.watchlistQueries.upsert(
                id = Id(id),
                created_at = dateFormatter.getTimestampMilliseconds(),
            )
        }
    }

    override fun getShowsInWatchlist(): List<Watchlists> =
        database.watchlistQueries.watchlists().executeAsList()

    override fun updateSyncState(id: Id<TmdbId>) {
        database.watchlistQueries.updateWatchlist(
            isSynced = true,
            id = id,
        )
    }

    override fun observeShowsInWatchlist(): Flow<List<Watchlists>> =
        database.watchlistQueries.watchlists()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> {
        return database.watchlistQueries
            .searchWatchlist(
                // Parameters for WHERE clause
                query,
                query,
                query,
                query,
                // Parameters for ORDER BY clause
                query,
                query,
                query,
            )
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeUnSyncedWatchlist(): Flow<List<Id<TmdbId>>> =
        database.watchlistQueries.unsyncedWatchlist()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun delete(id: Long) {
        database.watchlistQueries.delete(Id(id))
    }

    override fun upsert(entity: Show_metadata) {
        database.showMetadataQueries.upsert(
            show_id = entity.show_id,
            season_count = entity.season_count,
            episode_count = entity.episode_count,
            status = entity.status,
        )
    }
}
