package com.thomaskioko.tvmaniac.core.tasks.implementation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CancellationException

@AssistedInject
public class SchedulerDispatchWorker(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workerFactory: WorkerFactory,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
) : CoroutineWorker(context, params) {

    @AssistedFactory
    public fun interface Factory {
        public fun create(context: Context, params: WorkerParameters): SchedulerDispatchWorker
    }

    override suspend fun doWork(): Result {
        val workerName = inputData.getString(KEY_WORKER_NAME)
        if (workerName == null) {
            logger.error(TAG, "No worker name in input data")
            return Result.failure()
        }

        val worker = workerFactory.createWorker(workerName)
        if (worker == null) {
            logger.error(TAG, "No registered worker for name: $workerName")
            return Result.failure()
        }

        logger.debug(TAG, "Starting task [$workerName]")

        return try {
            when (worker.runTracked(workerName)) {
                is WorkerResult.Success -> {
                    logger.debug(TAG, "Task [$workerName] completed successfully")
                    Result.success()
                }
                is WorkerResult.Retry -> {
                    logger.debug(TAG, "Task [$workerName] requested retry")
                    Result.retry()
                }
                is WorkerResult.Failure -> {
                    logger.error(TAG, "Task [$workerName] failed")
                    Result.failure()
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.error(TAG, "Task [$workerName] threw: ${throwable.message}")
            Result.failure()
        }
    }

    private suspend fun BackgroundWorker.runTracked(workerName: String): WorkerResult =
        if (isLibrarySyncWork) {
            syncObserver.trackSync(workerName) { doWork() }
        } else {
            doWork()
        }

    internal companion object {
        internal const val KEY_WORKER_NAME = "worker_name"
        private const val TAG = "SchedulerDispatchWorker"
    }
}
