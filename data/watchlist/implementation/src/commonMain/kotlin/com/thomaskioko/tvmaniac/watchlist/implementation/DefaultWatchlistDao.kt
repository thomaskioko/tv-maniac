package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.SearchWatchlist
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.core.db.Watchlists
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchlistDao(
  private val database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

  override fun upsert(entity: Watchlist) {
    database.transaction {
      database.watchlistQueries.upsert(
        id = entity.id,
        created_at = entity.created_at,
      )
    }
  }

  override fun upsert(entities: List<Watchlist>) {
    entities.forEach { upsert(it) }
  }

  override fun getShowsInWatchlist(): List<Watchlists> =
    database.watchlistQueries.watchlists().executeAsList()

  override fun observeShowsInWatchlist(): Flow<List<Watchlists>> =
    database.watchlistQueries.watchlists()
      .asFlow()
      .mapToList(dispatchers.io)

  override fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> {
   return database.watchlistQueries
     .searchWatchlist(query, query, query, query)
     .asFlow()
     .mapToList(dispatchers.io)
  }

  override fun delete(id: Long) {
    database.watchlistQueries.delete(Id(id))
  }
}
