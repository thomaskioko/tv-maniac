package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

    override fun getShowsInWatchlist(): List<Watchlists> =
        database.watchlistQueries.watchlists().executeAsList()

    override fun observeShowsInWatchlist(): Flow<List<Watchlists>> =
        database.watchlistQueries.watchlists()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeShowsInWatchlistFiltered(includeSpecials: Boolean): Flow<List<Watchlists>> =
        database.watchlistQueries.watchlistsFiltered(includeSpecials = if (includeSpecials) 1L else 0L)
            .asFlow()
            .mapToList(dispatchers.io)
            .map { list ->
                list.map { row ->
                    Watchlists(
                        id = row.id,
                        name = row.name,
                        poster_path = row.poster_path,
                        first_air_date = row.first_air_date,
                        created_at = row.created_at,
                        season_count = row.season_count,
                        episode_count = row.episode_count,
                        status = row.status,
                        watched_count = row.watched_count,
                        total_episode_count = row.total_episode_count,
                    )
                }
            }

    override fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> =
        database.watchlistQueries
            .searchWatchlist(query = query)
            .asFlow()
            .mapToList(dispatchers.io)

    override fun delete(id: Long) {
        database.watchlistQueries.delete(Id(id))
    }

    override suspend fun isShowInLibrary(showId: Long): Boolean =
        withContext(dispatchers.io) {
            database.watchlistQueries.isShowInLibrary(Id(showId)).executeAsOne()
        }

    override fun observeIsShowInLibrary(showId: Long): Flow<Boolean> =
        database.watchlistQueries.isShowInLibrary(Id(showId))
            .asFlow()
            .mapToOne(dispatchers.io)
}
