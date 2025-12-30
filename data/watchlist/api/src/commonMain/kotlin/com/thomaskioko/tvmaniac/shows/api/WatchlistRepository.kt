package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import kotlinx.coroutines.flow.Flow

public interface WatchlistRepository {

    public fun observeWatchlist(): Flow<List<Watchlists>>

    public fun searchWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

    public fun observeUnSyncedItems(): Flow<Unit>

    /**
     * Observes the user's preferred list style for the watchlist.
     *
     * @return A Flow of Boolean indicating if grid mode is preferred (true for grid, false for list).
     */
    public fun observeListStyle(): Flow<Boolean>

    /**
     * Updates the library status of a show, adding or removing it from the user's watchlist.
     *
     * @param id The unique identifier of the show to be updated.
     * @param addToLibrary True to add the show to the library, false to remove it.
     */
    public suspend fun updateLibrary(id: Long, addToLibrary: Boolean)

    /**
     * Saves the user's preferred list style for the watchlist.
     *
     * @param isGridMode Boolean indicating if grid mode is preferred (true for grid, false for list).
     */
    public suspend fun saveListStyle(isGridMode: Boolean)

    /**
     * Checks if a show is currently in the user's library (watchlist).
     *
     * @param showId The ID of the show to check.
     * @return True if the show is in the library, false otherwise.
     */
    public suspend fun isShowInLibrary(showId: Long): Boolean
}
