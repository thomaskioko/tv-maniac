package com.thomaskioko.tvmaniac.watchlist.implementation.fixtures

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeWatchlistDao : WatchlistDao {

    private val watchlistFlow = MutableStateFlow<List<FollowedShows>>(emptyList())
    private val searchFlow = MutableStateFlow<List<SearchFollowedShows>>(emptyList())
    private val isFollowedFlow = MutableStateFlow(false)

    override fun observeShowsInWatchlist(): Flow<List<FollowedShows>> = watchlistFlow

    override fun observeWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>> = searchFlow

    override fun observeIsShowInLibrary(traktId: Long): Flow<Boolean> = isFollowedFlow

    fun setWatchlist(shows: List<FollowedShows>) {
        watchlistFlow.value = shows
    }
}
