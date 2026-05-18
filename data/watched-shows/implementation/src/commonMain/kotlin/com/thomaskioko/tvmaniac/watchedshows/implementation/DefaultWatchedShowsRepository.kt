package com.thomaskioko.tvmaniac.watchedshows.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedShowsRepository(
    private val watchedShowsStore: WatchedShowsStore,
) : WatchedShowsRepository {

    override suspend fun syncWatchedShows(forceRefresh: Boolean) {
        if (forceRefresh) {
            watchedShowsStore.fresh(key = Unit)
        } else {
            watchedShowsStore.get(key = Unit)
        }
    }
}
