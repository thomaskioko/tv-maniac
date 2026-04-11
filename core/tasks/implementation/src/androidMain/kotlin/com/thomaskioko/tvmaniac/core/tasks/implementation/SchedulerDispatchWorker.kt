package com.thomaskioko.tvmaniac.core.tasks.implementation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

public class SchedulerDispatchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workerFactory: WorkerFactory,
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

        return when (val result = worker.doWork()) {
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
    }

    internal companion object {
        internal const val KEY_WORKER_NAME = "worker_name"
        private const val TAG = "SchedulerDispatchWorker"
    }
}
