package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistInteractor(
    private val watchlistRepository: WatchlistRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Unit>() {

    override suspend fun doWork(params: Unit) {
        withContext(dispatchers.io) {
            watchlistRepository.observeUnSyncedItems()
                .collect()
        }
    }
}
