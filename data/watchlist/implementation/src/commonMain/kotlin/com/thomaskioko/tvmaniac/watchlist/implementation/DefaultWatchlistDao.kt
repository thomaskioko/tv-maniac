package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchlistDao(
    private val database: TvManiacDatabase,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

    override fun upsert(id: Long) {
        database.transaction {
            database.watchlistQueries.upsert(
                id = Id(id),
                created_at = dateTimeProvider.nowMillis(),
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

    override suspend fun isShowInLibrary(showId: Long): Boolean {
        return withContext(dispatchers.io) {
            database.watchlistQueries.isShowInLibrary(Id(showId)).executeAsOne()
        }
    }

    override fun observeIsShowInLibrary(showId: Long): Flow<Boolean> {
        return database.watchlistQueries.isShowInLibrary(Id(showId))
            .asFlow()
            .mapToOne(dispatchers.io)
    }
}
