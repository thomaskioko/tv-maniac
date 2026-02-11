package com.thomaskioko.tvmaniac.core.tasks.implementation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
public class DispatchingWorker(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scheduler: WorkManagerScheduler,
    private val logger: Logger,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val workerName = inputData.getString(KEY_WORKER_NAME)
        if (workerName == null) {
            logger.error(TAG, "No worker name in input data")
            return Result.failure()
        }

        val worker = scheduler.getWorker(workerName)
        if (worker == null) {
            logger.error(TAG, "No registered worker for name: $workerName")
            return Result.failure()
        }

        logger.debug(TAG, "Dispatching work to: $workerName")

        return when (worker.execute()) {
            WorkerResult.Success -> Result.success()
            WorkerResult.Retry -> Result.retry()
            WorkerResult.Failure -> Result.failure()
        }
    }

    internal companion object {
        internal const val KEY_WORKER_NAME = "worker_name"
        private const val TAG = "DispatchingWorker"
    }
}
