package com.thomaskioko.tvmaniac.episodes.api

public interface WatchedEpisodeSyncRepository {

    /**
     * Pushes locally-marked `pending_action='UPLOAD'` and `pending_action='DELETE'`
     * rows to Trakt. No remote read. Returns early when there are no pending rows
     * so the immediate UI-triggered push and the periodic worker stay cheap on
     * the idle path.
     */
    public suspend fun syncPendingEpisodes()

    /**
     * Pulls the full watched-shows state from Trakt's bulk `/sync/watched/shows`
     * endpoint (paginated, drained in 100-show pages) and upserts into the local
     * `watched_episodes` table. Gated by a two-step skip: when the bulk consumer's
     * activity checkpoint is current AND the per-feature TTL has not expired AND
     * [forceRefresh] is false, returns without any remote read.
     */
    public suspend fun syncAllWatchedEpisodes(forceRefresh: Boolean = false)

    /**
     * Per-show explicit refresh. Used by the show-details screen's pull-to-refresh
     * flow. With [forceRefresh] false the call short-circuits when the bulk
     * consumer is already caught up or the per-show TTL has not expired.
     */
    public suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean = false)
}
