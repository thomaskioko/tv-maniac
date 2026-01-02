package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeWatchlistRepository : WatchlistRepository {

    private val watchlistResult = MutableStateFlow<List<Watchlists>>(emptyList())
    private val searchlistResult = MutableStateFlow<List<SearchWatchlist>>(emptyList())
    private val listStyleFlow = MutableStateFlow(true)
    private val showsInLibrary = mutableSetOf<Long>()

    public fun setSearchResult(result: List<SearchWatchlist>) {
        searchlistResult.value = result
    }

    public fun setObserveResult(result: List<Watchlists>) {
        watchlistResult.value = result
    }

    override fun observeWatchlist(): Flow<List<Watchlists>> =
        watchlistResult

    override fun searchWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> =
        searchlistResult

    override fun observeListStyle(): Flow<Boolean> = listStyleFlow

    override suspend fun saveListStyle(isGridMode: Boolean) {
        listStyleFlow.value = isGridMode
    }
}
