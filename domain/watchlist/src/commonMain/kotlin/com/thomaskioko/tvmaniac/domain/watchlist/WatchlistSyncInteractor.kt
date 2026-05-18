package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class WatchlistSyncInteractor(
    private val traktActivityRepository: TraktActivityRepository,
    private val watchedShowsRepository: WatchedShowsRepository,
    private val fetchMissingShowsInteractor: FetchMissingShowsInteractor,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<WatchlistSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            traktActivityRepository.fetchLatestActivities(params.forceRefresh)
            watchedShowsRepository.syncWatchedShows(params.forceRefresh)
            fetchMissingShowsInteractor.executeSync(params.forceRefresh)
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "WatchlistSyncInteractor"
    }
}
