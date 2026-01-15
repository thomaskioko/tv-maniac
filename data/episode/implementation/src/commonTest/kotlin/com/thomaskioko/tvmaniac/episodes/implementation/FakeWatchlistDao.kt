package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeWatchlistDao : WatchlistDao {
    private val isInLibraryFlow = MutableStateFlow(true)

    fun setIsInLibrary(inLibrary: Boolean) {
        isInLibraryFlow.value = inLibrary
    }

    override fun observeShowsInWatchlist(): Flow<List<FollowedShows>> = MutableStateFlow(emptyList())

    override fun observeWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>> =
        MutableStateFlow(emptyList())

    override fun observeIsShowInLibrary(traktId: Long): Flow<Boolean> = isInLibraryFlow
}
