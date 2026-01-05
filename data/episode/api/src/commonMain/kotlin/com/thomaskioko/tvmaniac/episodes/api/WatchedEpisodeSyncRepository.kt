package com.thomaskioko.tvmaniac.episodes.api

import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeSyncRepository {
    public suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean = false)
    public fun observePendingSyncCount(): Flow<Long>
}
