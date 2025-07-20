package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Inject
class WatchlistSyncer(
    private val watchlistRepository: WatchlistRepository,
    private val coroutineScope: AppCoroutineScope,
) : AppInitializer {
    override fun init() {
        // TODO: Run this in a task/worker.
        coroutineScope.io.launch {
            watchlistRepository.observeUnSyncedItems()
                .collect()
        }
    }
}
