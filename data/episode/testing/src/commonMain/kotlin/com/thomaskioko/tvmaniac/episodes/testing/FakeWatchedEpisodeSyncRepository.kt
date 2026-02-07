package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository

public class FakeWatchedEpisodeSyncRepository : WatchedEpisodeSyncRepository {
    private var lastSyncedShowId: Long? = null
    private var lastForceRefresh: Boolean = false

    public fun getLastSyncedShowId(): Long? = lastSyncedShowId

    public fun wasForceRefreshUsed(): Boolean = lastForceRefresh

    public fun reset() {
        lastSyncedShowId = null
        lastForceRefresh = false
    }

    override suspend fun syncShowEpisodeWatches(showTraktId: Long, forceRefresh: Boolean) {
        lastSyncedShowId = showTraktId
        lastForceRefresh = forceRefresh
    }

    override suspend fun uploadPendingEpisodes() {
    }
}
