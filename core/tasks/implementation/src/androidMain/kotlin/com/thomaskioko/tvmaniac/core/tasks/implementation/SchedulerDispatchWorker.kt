package com.thomaskioko.tvmaniac.core.tasks.implementation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CancellationException

@AssistedInject
public class SchedulerDispatchWorker(
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

        return try {
            when (val result = worker.doWork()) {
                is WorkerResult.Success -> {
                    logger.debug(TAG, "Task [$workerName] completed successfully")
                    Result.success()
                }
                is WorkerResult.Retry -> {
                    val keys = reportKeys(workerName, "retry")
                    result.cause?.let { logger.warning(TAG, "Task [$workerName] requested retry", it, keys) }
                        ?: logger.warning(TAG, "Task [$workerName] requested retry")
                    Result.retry()
                }
                is WorkerResult.Failure -> {
                    val keys = reportKeys(workerName, "failure")
                    result.cause?.let { logger.error(TAG, "Task [$workerName] failed", it, keys) }
                        ?: logger.error(TAG, "Task [$workerName] failed")
                    Result.failure()
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.error(TAG, "Task [$workerName] threw: ${throwable.message}", throwable, reportKeys(workerName, "threw"))
            Result.failure()
        }
    }

    private fun reportKeys(workerName: String, result: String): Map<String, String> = mapOf(
        CrashReportKeys.WORKER to workerName,
        CrashReportKeys.ATTEMPT to runAttemptCount.toString(),
        CrashReportKeys.RESULT to result,
    )

    internal companion object {
        internal const val KEY_WORKER_NAME = "worker_name"
        private const val TAG = "SchedulerDispatchWorker"
    }
}
