package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeWatchlistDao : WatchlistDao {
    private val isInLibraryFlow = MutableStateFlow(true)

    fun setIsInLibrary(inLibrary: Boolean) {
        isInLibraryFlow.value = inLibrary
    }

    override fun getShowsInWatchlist(): List<Watchlists> = emptyList()

    override fun observeShowsInWatchlist(): Flow<List<Watchlists>> = MutableStateFlow(emptyList())

    override fun observeShowsInWatchlistFiltered(includeSpecials: Boolean): Flow<List<Watchlists>> =
        MutableStateFlow(emptyList())

    override fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> =
        MutableStateFlow(emptyList())

    override fun delete(id: Long) {}

    override suspend fun isShowInLibrary(showId: Long): Boolean = isInLibraryFlow.value

    override fun observeIsShowInLibrary(showId: Long): Flow<Boolean> = isInLibraryFlow
}
