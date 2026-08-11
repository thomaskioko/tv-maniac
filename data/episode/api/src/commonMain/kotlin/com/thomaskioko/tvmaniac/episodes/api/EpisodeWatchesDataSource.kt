package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider

public interface EpisodeWatchesDataSource : SyncProvider {

    public suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry>

    public suspend fun getAllWatchedShows(page: Int = 1, limit: Int = 100): List<WatchedShowBatch>

    /**
     * Runtime, release year and genres for the user's watched shows. Providers that already return
     * these from [getAllWatchedShows] keep the default and cost no extra request.
     */
    public suspend fun getWatchedShowMetadata(page: Int = 1, limit: Int = 100): List<WatchedShowMetadata> = emptyList()

    public suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>)

    public suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>)
}
