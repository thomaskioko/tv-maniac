package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class WatchlistSyncer(
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
