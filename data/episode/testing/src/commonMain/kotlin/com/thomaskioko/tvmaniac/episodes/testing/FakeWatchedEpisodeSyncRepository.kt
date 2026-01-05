package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository

public class FakeWatchedEpisodeSyncRepository : WatchedEpisodeSyncRepository {
    override suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean) {
    }
}
