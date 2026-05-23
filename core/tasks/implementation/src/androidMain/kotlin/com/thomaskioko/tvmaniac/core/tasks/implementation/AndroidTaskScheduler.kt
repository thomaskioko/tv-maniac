package com.thomaskioko.tvmaniac.core.tasks.implementation

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import java.util.concurrent.TimeUnit

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidTaskScheduler(
    workManager: Lazy<WorkManager>,
    private val logger: Logger,
) : BackgroundTaskScheduler {

    private val workManager by workManager

    override fun schedulePeriodic(request: PeriodicTaskRequest) {
        val work = PeriodicWorkRequestBuilder<SchedulerDispatchWorker>(
            request.intervalMs,
            TimeUnit.MILLISECONDS,
        )
            .setConstraints(request.constraints.toWorkManagerConstraints())
            .setInputData(workDataOf(SchedulerDispatchWorker.KEY_WORKER_NAME to request.id))
            .build()

        workManager.enqueueUniquePeriodicWork(
            request.id,
            ExistingPeriodicWorkPolicy.UPDATE,
            work,
        )

        logger.debug(TAG, "Scheduled periodic task [${request.id}]")
    }

    override fun cancel(id: String) {
        workManager.cancelUniqueWork(id)
        logger.debug(TAG, "Cancelled task [$id]")
    }

    override fun cancelAll() {
        workManager.cancelAllWork()
        logger.debug(TAG, "Cancelled all tasks")
    }

    private companion object {
        private const val TAG = "AndroidTaskScheduler"

        private fun TaskConstraints.toWorkManagerConstraints(): Constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    when {
                        requiresUnmeteredNetwork -> NetworkType.UNMETERED
                        requiresNetwork -> NetworkType.CONNECTED
                        else -> NetworkType.NOT_REQUIRED
                    },
                )
                .build()
    }
}
