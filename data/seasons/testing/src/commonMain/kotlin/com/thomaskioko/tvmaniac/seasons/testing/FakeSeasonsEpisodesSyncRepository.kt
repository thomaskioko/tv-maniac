package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.seasons.api.SeasonsEpisodesSyncRepository

public class FakeSeasonsEpisodesSyncRepository : SeasonsEpisodesSyncRepository {

    private val _syncedShowIds = mutableListOf<Long>()
    private var error: Throwable? = null

    public val syncedShowIds: List<Long>
        get() = _syncedShowIds

    public fun setSyncError(throwable: Throwable?) {
        error = throwable
    }

    override suspend fun syncSeasonsWithEpisodes(showId: Long, forceRefresh: Boolean) {
        error?.let { throw it }
        _syncedShowIds.add(showId)
    }
}
