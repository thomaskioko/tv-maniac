package com.thomaskioko.tvmaniac.upnext.testing

import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeUpNextRepository : UpNextRepository {
    private val nextEpisodesForWatchlist = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())
    private val upNextSortOptionFlow = MutableStateFlow("LAST_WATCHED")

    public fun setNextEpisodesForWatchlist(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesForWatchlist.value = episodes
    }

    public fun setUpNextSortOption(sortOption: String) {
        upNextSortOptionFlow.value = sortOption
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesForWatchlist.asStateFlow()

    override suspend fun saveUpNextSortOption(sortOption: String) {
        upNextSortOptionFlow.value = sortOption
    }

    override fun observeUpNextSortOption(): Flow<String> = upNextSortOptionFlow.asStateFlow()
}
