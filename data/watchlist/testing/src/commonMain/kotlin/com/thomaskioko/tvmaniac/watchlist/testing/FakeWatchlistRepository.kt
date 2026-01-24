package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration

public class FakeWatchlistRepository : WatchlistRepository {

    private val watchlistResult = MutableStateFlow<List<FollowedShows>>(emptyList())
    private val searchlistResult = MutableStateFlow<List<SearchFollowedShows>>(emptyList())
    private val listStyleFlow = MutableStateFlow(true)
    private var needsSyncResult = true

    public fun setSearchResult(result: List<SearchFollowedShows>) {
        searchlistResult.value = result
    }

    public fun setObserveResult(result: List<FollowedShows>) {
        watchlistResult.value = result
    }

    public fun setNeedsSyncResult(value: Boolean) {
        needsSyncResult = value
    }

    override fun observeWatchlist(): Flow<List<FollowedShows>> =
        watchlistResult

    override fun searchWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>> =
        searchlistResult

    override fun observeListStyle(): Flow<Boolean> = listStyleFlow

    override suspend fun saveListStyle(isGridMode: Boolean) {
        listStyleFlow.value = isGridMode
    }

    override suspend fun syncWatchlist(forceRefresh: Boolean) {
    }

    override suspend fun needsSync(expiry: Duration): Boolean = needsSyncResult
}
