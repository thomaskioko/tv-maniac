package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository

public class FakeWatchedEpisodeSyncRepository : WatchedEpisodeSyncRepository {
    private val syncedShowIds = mutableListOf<Long>()
    private var lastForceRefresh: Boolean = false
    private var pendingError: Throwable? = null

    public fun getLastSyncedShowId(): Long? = syncedShowIds.lastOrNull()

    public fun getSyncedShowIds(): List<Long> = syncedShowIds.toList()

    public fun wasForceRefreshUsed(): Boolean = lastForceRefresh

    public fun setPendingEpisodesError(error: Throwable?) {
        pendingError = error
    }

    public fun reset() {
        syncedShowIds.clear()
        lastForceRefresh = false
        pendingError = null
    }

    override suspend fun syncShowEpisodeWatches(showTraktId: Long, forceRefresh: Boolean) {
        syncedShowIds.add(showTraktId)
        lastForceRefresh = forceRefresh
    }

    override suspend fun syncPendingEpisodes() {
        pendingError?.let { throw it }
    }
}
