package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import kotlinx.coroutines.flow.Flow

public interface WatchlistRepository {

    public fun observeWatchlist(): Flow<List<Watchlists>>

    public fun searchWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

    public fun observeListStyle(): Flow<Boolean>

    /**
     * Saves the user's preferred list style for the watchlist.
     *
     * @param isGridMode Boolean indicating if grid mode is preferred (true for grid, false for list).
     */
    public suspend fun saveListStyle(isGridMode: Boolean)
}
