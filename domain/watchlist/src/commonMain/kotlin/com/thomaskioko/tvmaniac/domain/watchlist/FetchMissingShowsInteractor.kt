package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class FetchMissingShowsInteractor(
    private val syncWatchedShowInteractor: SyncWatchedShowInteractor,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<WatchlistSections>() {

    private val fetchedShowIds = mutableSetOf<Long>()

    override suspend fun doWork(params: WatchlistSections) {
        withContext(dispatchers.io) {
            val pendingIds = (params.watchNext + params.stale)
                .filter { it.title == null }
                .map { it.traktId }
                .filter { fetchedShowIds.add(it) }
            pendingIds.forEach { traktId ->
                syncWatchedShowInteractor.executeSync(
                    SyncWatchedShowInteractor.Param(traktId = traktId, forceRefresh = false),
                )
            }
        }
    }

    private companion object {
        private const val TAG = "FetchMissingShowsInteractor"
    }
}
