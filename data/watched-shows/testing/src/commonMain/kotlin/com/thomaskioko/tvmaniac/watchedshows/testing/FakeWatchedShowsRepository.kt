package com.thomaskioko.tvmaniac.watchedshows.testing

import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsRepository
import com.thomaskioko.tvmaniac.watchedshows.implementation.DefaultWatchedShowsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultWatchedShowsRepository::class])
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
