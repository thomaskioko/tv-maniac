package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.CancellationException
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = BackgroundWorker::class, multibinding = true)
public class LibrarySyncWorker(
    private val syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val dateTimeProvider: Lazy<DateTimeProvider>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Library sync worker starting")

        if (!traktAuthRepository.value.isLoggedIn()) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return WorkerResult.Success
        }

        return try {
            syncLibraryInteractor.value.executeSync(
                SyncLibraryInteractor.Param(forceRefresh = true),
            )
            datastoreRepository.value.setLastSyncTimestamp(dateTimeProvider.value.nowMillis())
            logger.debug(TAG, "Library sync completed successfully")
            WorkerResult.Success
        } catch (e: CancellationException) {
            logger.debug(TAG, "Library sync cancelled: ${e.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (e: Exception) {
            logger.error(TAG, "Library sync failed: ${e.message}")
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
        )
    }
}
