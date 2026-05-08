package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException

/**
 * Drains rows stuck in `pending_action='UPLOAD'` or `pending_action='DELETE'` by invoking
 * [WatchedEpisodeSyncRepository.syncPendingEpisodes]. Scheduled by `SyncTasksInitializer`
 * alongside `LibrarySyncWorker` whenever the user is logged in and background sync is enabled.
 *
 * Returns [WorkerResult.Retry] on transient failures so the platform scheduler (WorkManager on
 * Android, BGTaskScheduler on iOS) applies its native exponential backoff.
 *
 * Why 15 minutes:
 * - `WorkManager.PeriodicWorkRequest` rejects intervals shorter than 15 minutes; this is the
 *   shortest cadence the platform accepts.
 * - 15 minutes bounds how long a pending push can sit before the next retry attempt, which
 *   matches the snackbar's "will retry when you're back online" promise without being so
 *   aggressive that it drains battery while the user is offline.
 * - Network and idle constraints (`requiresNetwork = true`) gate execution, so the worker
 *   doesn't fire when there's no network or the device is throttling.
 */
@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class PendingUploadsWorker(
    private val syncRepository: Lazy<WatchedEpisodeSyncRepository>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Pending uploads worker starting")

        if (!traktAuthRepository.value.isLoggedIn()) {
            logger.debug(TAG, "User not logged in, skipping pending uploads sync")
            return WorkerResult.Success
        }

        return try {
            syncRepository.value.syncPendingEpisodes()
            logger.debug(TAG, "Pending uploads sync completed successfully")
            WorkerResult.Success
        } catch (cancellation: CancellationException) {
            logger.debug(TAG, "Pending uploads sync cancelled: ${cancellation.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (exception: Exception) {
            logger.error(TAG, "Pending uploads sync failed: ${exception.message}")
            WorkerResult.Retry(exception.message ?: "Pending uploads sync failed")
        }
    }

    public companion object {
        public const val WORKER_NAME: String = "com.thomaskioko.tvmaniac.pendinguploads"
        private const val TAG = "PendingUploadsWorker"
        private const val FIFTEEN_MINUTES_MS = 15L * 60 * 1000

        public val REQUEST: PeriodicTaskRequest = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = FIFTEEN_MINUTES_MS,
            constraints = TaskConstraints(requiresNetwork = true),
        )
    }
}
