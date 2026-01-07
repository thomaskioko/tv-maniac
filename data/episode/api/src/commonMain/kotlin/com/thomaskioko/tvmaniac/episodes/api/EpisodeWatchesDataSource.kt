package com.thomaskioko.tvmaniac.episodes.api

public interface EpisodeWatchesDataSource {
    public suspend fun getShowEpisodeWatches(showTraktId: Long): List<WatchedEpisodeEntry>
    public suspend fun addEpisodeWatches(watches: List<WatchedEpisodeEntry>)
    public suspend fun removeEpisodeWatches(traktHistoryIds: List<Long>)
}
