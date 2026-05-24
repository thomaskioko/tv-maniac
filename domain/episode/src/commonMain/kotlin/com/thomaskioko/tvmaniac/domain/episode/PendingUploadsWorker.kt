package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException


@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class PendingUploadsWorker(
    private val syncRepository: Lazy<WatchedEpisodeSyncRepository>,
    private val libraryRepository: Lazy<LibraryRepository>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val syncObserver: SyncObserver,
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
            libraryRepository.value.syncPendingFollowedShows()
            logger.debug(TAG, "Pending uploads sync completed successfully")
            WorkerResult.Success
        } catch (cancellation: CancellationException) {
            logger.debug(TAG, "Pending uploads sync cancelled: ${cancellation.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (exception: Exception) {
            logger.error(TAG, "Pending uploads sync failed: ${exception.message}")
            syncObserver.log(SyncError.BackgroundSyncFailed(WORKER_NAME, exception))
            WorkerResult.Retry(exception.message ?: "Pending uploads sync failed")
        }
    }

    public companion object {
        public const val WORKER_NAME: String = "com.thomaskioko.tvmaniac.pendinguploads"
        private const val TAG = "PendingUploadsWorker"
        private const val SIX_HOURS_MS = 6L * 60 * 60 * 1000

        public val REQUEST: PeriodicTaskRequest = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = SIX_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
        )
    }
}
