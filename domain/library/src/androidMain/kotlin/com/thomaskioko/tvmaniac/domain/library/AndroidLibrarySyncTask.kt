package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorkerScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.NetworkRequirement
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.CancellationException
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = SyncTasks::class)
public class AndroidLibrarySyncTask(
    private val scheduler: BackgroundWorkerScheduler,
    private val syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val dateTimeProvider: Lazy<DateTimeProvider>,
    private val logger: Logger,
) : SyncTasks, BackgroundWorker {

    override val workerName: String = WORKER_NAME
    override val interval: Duration = SYNC_INTERVAL
    override val constraints: WorkerConstraints = WorkerConstraints(NetworkRequirement.UNMETERED)

    override fun setup() {
        scheduler.register(this)
    }

    override fun scheduleLibrarySync() {
        scheduler.schedulePeriodic(workerName)
    }

    override fun cancelLibrarySync() {
        scheduler.cancel(workerName)
    }

    override suspend fun execute(): WorkerResult {
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
            logger.debug(
                TAG,
                "Library sync cancelled, will retry when constraints are met. : ${e.message}",
            )
            WorkerResult.Retry
        } catch (e: Exception) {
            logger.error(TAG, "Library sync failed: ${e.message}")
            WorkerResult.Failure
        }
    }

    private companion object {
        private const val TAG = "AndroidLibrarySyncTask"
        private const val WORKER_NAME = "library_sync_worker"
        private val SYNC_INTERVAL = 12.hours
    }
}
