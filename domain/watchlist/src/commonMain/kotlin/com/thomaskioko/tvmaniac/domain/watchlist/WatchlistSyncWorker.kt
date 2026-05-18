package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class WatchlistSyncWorker(
    private val watchlistSyncInteractor: Lazy<WatchlistSyncInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Watchlist sync worker starting")

        if (!traktAuthRepository.value.isLoggedIn()) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return WorkerResult.Success
        }

        return try {
            watchlistSyncInteractor.value.executeSync(
                WatchlistSyncInteractor.Param(forceRefresh = true),
            )
            logger.debug(TAG, "Watchlist sync completed successfully")
            WorkerResult.Success
        } catch (e: CancellationException) {
            logger.debug(TAG, "Watchlist sync cancelled: ${e.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (e: Exception) {
            logger.error(TAG, "Watchlist sync failed: ${e.message}")
            syncObserver.log(SyncError.BackgroundSyncFailed(WORKER_NAME, e))
            WorkerResult.Failure(e.message)
        }
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.watchlistsync"
        private const val TAG = "WatchlistSyncWorker"
        private const val TWELVE_HOURS_MS = 12L * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = TWELVE_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
        )
    }
}
