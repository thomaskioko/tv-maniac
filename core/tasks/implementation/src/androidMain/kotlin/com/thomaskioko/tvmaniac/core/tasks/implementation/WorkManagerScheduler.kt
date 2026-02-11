package com.thomaskioko.tvmaniac.core.tasks.implementation

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorkerScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.NetworkRequirement
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerConstraints
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.toJavaDuration

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class WorkManagerScheduler(
    workManager: Lazy<WorkManager>,
    private val logger: Logger,
) : BackgroundWorkerScheduler {
    private val workManager by workManager
    private val registeredWorkers = mutableMapOf<String, BackgroundWorker>()

    override fun register(worker: BackgroundWorker) {
        registeredWorkers[worker.workerName] = worker
        logger.debug(TAG, "Registered worker: ${worker.workerName}")
    }

    override fun schedulePeriodic(workerName: String) {
        val worker = registeredWorkers[workerName] ?: run {
            logger.error(TAG, "Cannot schedule unregistered worker: $workerName")
            return
        }
        logger.debug(TAG, "Scheduling periodic work: $workerName")

        val work = PeriodicWorkRequestBuilder<DispatchingWorker>(worker.interval.toJavaDuration())
            .setConstraints(worker.constraints.toWorkManagerConstraints())
            .setInputData(workDataOf(DispatchingWorker.KEY_WORKER_NAME to worker.workerName))
            .build()

        workManager.enqueueUniquePeriodicWork(
            worker.workerName,
            ExistingPeriodicWorkPolicy.UPDATE,
            work,
        )
    }

    override fun scheduleImmediate(workerName: String) {
        val worker = registeredWorkers[workerName] ?: run {
            logger.error(TAG, "Cannot schedule unregistered worker: $workerName")
            return
        }
        logger.debug(TAG, "Scheduling immediate work: $workerName")

        val work = OneTimeWorkRequestBuilder<DispatchingWorker>()
            .setConstraints(worker.constraints.toWorkManagerConstraints())
            .setInputData(workDataOf(DispatchingWorker.KEY_WORKER_NAME to worker.workerName))
            .build()

        workManager.enqueueUniqueWork(
            "${worker.workerName}_immediate",
            ExistingWorkPolicy.REPLACE,
            work,
        )
    }

    override fun scheduleAndExecute(workerName: String) {
        scheduleImmediate(workerName)
        schedulePeriodic(workerName)
    }

    override fun cancel(workerName: String) {
        logger.debug(TAG, "Cancelling work: $workerName")
        workManager.cancelUniqueWork(workerName)
        workManager.cancelUniqueWork("${workerName}_immediate")
    }

    internal fun getWorker(workerName: String): BackgroundWorker? = registeredWorkers[workerName]

    private companion object {
        private const val TAG = "WorkManagerScheduler"

        private fun WorkerConstraints.toWorkManagerConstraints(): Constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    when (networkType) {
                        NetworkRequirement.NOT_REQUIRED -> NetworkType.NOT_REQUIRED
                        NetworkRequirement.CONNECTED -> NetworkType.CONNECTED
                        NetworkRequirement.UNMETERED -> NetworkType.UNMETERED
                    },
                )
                .build()
    }
}
