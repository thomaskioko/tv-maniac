package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException

/**
 * Periodic background worker that runs the full library sync (watchlist, watched episodes, and
 * per-show metadata fan-out). Scheduled by [SyncTasksInitializer] while the user is logged in and
 * background sync is enabled. Runs only in the background, never inline at app start.
 */
@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class LibrarySyncWorker(
    private val syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    private val accountManager: Lazy<AccountManager>,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Library sync worker starting")

        if (accountManager.value.getActiveProvider() == null) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return WorkerResult.Success
        }

        return try {
            syncLibraryInteractor.value.executeSync(
                SyncLibraryInteractor.Param(forceRefresh = true),
            )
            logger.debug(TAG, "Library sync completed successfully")
            WorkerResult.Success
        } catch (e: CancellationException) {
            logger.debug(TAG, "Library sync cancelled: ${e.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (e: Exception) {
            logger.error(TAG, "Library sync failed: ${e.message}")
            syncObserver.log(SyncError.BackgroundSyncFailed(WORKER_NAME, e))
            WorkerResult.Failure(e.message)
        }
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.librarysync"
        private const val TAG = "LibrarySyncWorker"
        private const val TWELVE_HOURS_MS = 12L * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = TWELVE_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
            longRunning = true,
        )
    }
}
