package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class RestoredShowsRefillWorker(
    private val syncRestoredShowsInteractor: Lazy<SyncRestoredShowsInteractor>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult = try {
        syncRestoredShowsInteractor.value.executeSync(Unit)
        WorkerResult.Success
    } catch (cancellation: CancellationException) {
        logger.debug(TAG, "Metadata refill cancelled: ${cancellation.message}")
        WorkerResult.Retry("Cancelled, will retry")
    } catch (cause: Exception) {
        logger.error(TAG, "Metadata refill failed: ${cause.message}")
        WorkerResult.Failure(cause.message)
    }

    public companion object {
        public const val WORKER_NAME: String = "com.thomaskioko.tvmaniac.showrefill"
        private const val TAG = "RestoredShowsRefillWorker"
        private const val TWELVE_HOURS_MS = 12L * 60 * 60 * 1000

        public val REQUEST: PeriodicTaskRequest = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = TWELVE_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
            longRunning = true,
        )
    }
}
