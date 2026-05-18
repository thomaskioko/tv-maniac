package com.thomaskioko.tvmaniac.watchedshows.testing

import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsRepository

public class FakeWatchedShowsRepository : WatchedShowsRepository {

    private val syncInvocations = mutableListOf<Boolean>()

    public fun syncInvocations(): List<Boolean> = syncInvocations.toList()

    public fun clearInvocations() {
        syncInvocations.clear()
    }

    override suspend fun syncWatchedShows(forceRefresh: Boolean) {
        syncInvocations.add(forceRefresh)
    }
}
