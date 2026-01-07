package com.thomaskioko.tvmaniac.episodes.api

public interface WatchedEpisodeSyncRepository {
    public suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean = false)
}
