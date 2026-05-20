package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

@Inject
public class WatchlistSyncInteractor(
    private val traktActivityRepository: TraktActivityRepository,
    private val continueWatchingRepository: ContinueWatchingRepository,
    private val continueWatchingDao: ContinueWatchingDao,
    private val syncWatchedShowInteractor: SyncWatchedShowInteractor,
    private val syncObserver: SyncObserver,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<WatchlistSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        syncObserver.trackSync(TAG) {
            withContext(dispatchers.io) {
                traktActivityRepository.fetchLatestActivities(params.forceRefresh)
                continueWatchingRepository.sync(
                    forceRefresh = params.forceRefresh,
                    useNitro = params.useNitro,
                )

                val watchedShows = continueWatchingDao.entries()
                logger.debug(TAG, "Syncing metadata for ${watchedShows.size} watched shows")

                watchedShows.parallelForEach(concurrency = WATCHLIST_SYNC_CONCURRENCY) { show ->
                    ensureActive()
                    runCatching {
                        syncWatchedShowInteractor.executeSync(
                            SyncWatchedShowInteractor.Param(
                                traktId = show.traktId,
                                forceRefresh = params.forceRefresh,
                            ),
                        )
                    }.onFailure {
                        logger.warning(TAG, "syncWatchedShow failed for ${show.traktId}: ${it.message}")
                        syncObserver.log(SyncError.BackgroundSyncFailed(TAG, it))
                    }
                }

                logger.debug(TAG, "Watchlist sync complete")
            }
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
        val useNitro: Boolean = false,
    )

    private companion object {
        private const val TAG = "WatchlistSyncInteractor"
        private const val WATCHLIST_SYNC_CONCURRENCY = 2
    }
}
