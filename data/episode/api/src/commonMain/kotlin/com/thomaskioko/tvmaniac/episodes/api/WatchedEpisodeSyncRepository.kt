package com.thomaskioko.tvmaniac.episodes.api

public interface WatchedEpisodeSyncRepository {
    public suspend fun syncShowEpisodeWatches(showTraktId: Long, forceRefresh: Boolean = false)
    public suspend fun uploadPendingEpisodes()
}
