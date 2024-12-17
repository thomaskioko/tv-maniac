package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SearchWatchlist
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.core.db.Watchlists
import kotlinx.coroutines.flow.Flow

interface WatchlistDao {

  fun upsert(entity: Watchlist)

  fun upsert(entities: List<Watchlist>)

  fun getShowsInWatchlist(): List<Watchlists>

  fun observeShowsInWatchlist(): Flow<List<Watchlists>>

  fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

  fun delete(id: Long)
}
