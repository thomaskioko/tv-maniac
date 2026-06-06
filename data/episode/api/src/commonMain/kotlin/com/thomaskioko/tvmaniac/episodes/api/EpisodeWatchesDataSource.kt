package com.thomaskioko.tvmaniac.episodes.api

public interface EpisodeWatchesDataSource {
    public suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry>

    /**
     * Fetches a single page of Trakt's bulk `/sync/watched/shows` endpoint
     * with `extended=full`. Each [WatchedShowBatch] in the returned list
     * carries the per-show episode list. Pagination is the caller's
     * responsibility: keep requesting pages with `limit = 100` until a
     * page returns fewer than `limit` shows.
     *
     * Throws on transport failure so the caller can skip writing the
     * consumer checkpoint and retry on the next sync.
     */
    public suspend fun getAllWatchedShows(page: Int = 1, limit: Int = 100): List<WatchedShowBatch>

    public suspend fun addEpisodeWatches(watches: List<WatchedEpisodeEntry>)

    /**
     * Removes every play of each episode keyed by its Trakt episode id.
     * Trakt's `/sync/history/remove` deletes all plays of the targeted
     * episode in a single call, which matches the app's "unwatch" intent
     * (one episode toggle removes every prior play of that episode).
     */
    public suspend fun removeEpisodeWatches(episodeTraktIds: List<Long>)
}
