package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeNextEpisodeDao : NextEpisodeDao {

    private val nextEpisodesFlow = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())

    public fun setNextEpisodes(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesFlow.value = episodes
    }

    override fun observeNextEpisodesForWatchlist(includeSpecials: Boolean): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesFlow.asStateFlow()
}
