package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider

public interface EpisodeWatchesDataSource : SyncProvider {

    public suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry>

    public suspend fun getAllWatchedShows(page: Int = 1, limit: Int = 100): List<WatchedShowBatch>

    public suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>)

    public suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>)
}
