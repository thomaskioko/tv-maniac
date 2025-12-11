package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    fun observeWatchlist(): Flow<List<Watchlists>>

    fun searchWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

    fun observeUnSyncedItems(): Flow<Unit>

    suspend fun updateLibrary(id: Long, addToLibrary: Boolean)

    /**
     * Observes the user's preferred list style for the watchlist.
     *
     * @return A Flow of Boolean indicating if grid mode is preferred (true for grid, false for list).
     */
    fun observeListStyle(): Flow<Boolean>

    /**
     * Saves the user's preferred list style for the watchlist.
     *
     * @param isGridMode Boolean indicating if grid mode is preferred (true for grid, false for list).
     */
    suspend fun saveListStyle(isGridMode: Boolean)

    /**
     * Checks if a show is currently in the user's library (watchlist).
     *
     * @param showId The ID of the show to check.
     * @return True if the show is in the library, false otherwise.
     */
    suspend fun isShowInLibrary(showId: Long): Boolean
}
