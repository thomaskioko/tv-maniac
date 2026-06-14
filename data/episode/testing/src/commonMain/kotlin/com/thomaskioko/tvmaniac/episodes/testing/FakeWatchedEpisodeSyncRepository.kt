package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository

public class FakeWatchedEpisodeSyncRepository : WatchedEpisodeSyncRepository {
    private val syncedShowIds = mutableListOf<Long>()
    private val syncAllForceRefreshArgs = mutableListOf<Boolean>()
    private var lastForceRefresh: Boolean = false
    private var pendingError: Throwable? = null
    private var syncAllError: Throwable? = null
    private var syncPendingCallCount = 0

    public fun getLastSyncedShowId(): Long? = syncedShowIds.lastOrNull()

    public fun getSyncedShowIds(): List<Long> = syncedShowIds.toList()

    public fun wasForceRefreshUsed(): Boolean = lastForceRefresh

    public fun syncAllInvocations(): List<Boolean> = syncAllForceRefreshArgs.toList()

    public fun syncPendingCallCount(): Int = syncPendingCallCount

    public fun setPendingEpisodesError(error: Throwable?) {
        pendingError = error
    }

    public fun setSyncAllError(error: Throwable?) {
        syncAllError = error
    }

    public fun reset() {
        syncedShowIds.clear()
        syncAllForceRefreshArgs.clear()
        lastForceRefresh = false
        pendingError = null
        syncAllError = null
        syncPendingCallCount = 0
    }

    override suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean) {
        syncedShowIds.add(showId)
        lastForceRefresh = forceRefresh
    }

    override suspend fun syncAllWatchedEpisodes(forceRefresh: Boolean) {
        syncAllError?.let { throw it }
        syncAllForceRefreshArgs.add(forceRefresh)
    }

    override suspend fun syncPendingEpisodes() {
        syncPendingCallCount++
        pendingError?.let { throw it }
    }

    private var pendingEpisodesCount = 0L

    public fun setPendingEpisodesCount(count: Long) {
        pendingEpisodesCount = count
    }

    override suspend fun countPendingEpisodes(): Long = pendingEpisodesCount
}
