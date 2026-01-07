package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry

public class FakeEpisodeWatchesDataSource : EpisodeWatchesDataSource {
    private val watchesMap = mutableMapOf<Long, List<WatchedEpisodeEntry>>()

    override suspend fun getShowEpisodeWatches(showTraktId: Long): List<WatchedEpisodeEntry> {
        return watchesMap[showTraktId] ?: emptyList()
    }

    override suspend fun addEpisodeWatches(watches: List<WatchedEpisodeEntry>) {
    }

    override suspend fun removeEpisodeWatches(traktHistoryIds: List<Long>) {
    }
}
