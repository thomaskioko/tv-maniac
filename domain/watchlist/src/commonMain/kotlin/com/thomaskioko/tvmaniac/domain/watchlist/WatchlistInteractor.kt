package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.collect
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistInteractor(
    private val watchlistRepository: WatchlistRepository,
) : Interactor<Unit>() {

    override suspend fun doWork(params: Unit) {
        watchlistRepository.observeUnSyncedItems()
            .collect()
    }
}
