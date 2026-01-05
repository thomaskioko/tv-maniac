package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeWatchedEpisodeSyncRepository : WatchedEpisodeSyncRepository {
    private val pendingSyncCount = MutableStateFlow(0L)

    public fun setPendingSyncCount(count: Long) {
        pendingSyncCount.value = count
    }

    override suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean) {
    }

    override fun observePendingSyncCount(): Flow<Long> = pendingSyncCount.asStateFlow()
}
