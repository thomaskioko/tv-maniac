package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosTaskScheduler(
    private val workerFactory: WorkerFactory,
    private val appCoroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : BackgroundTaskScheduler {

    private val scheduler = BGTaskScheduler.sharedScheduler
    private val activeRequests = mutableMapOf<String, PeriodicTaskRequest>()
    private val registeredTaskIds = mutableSetOf<String>()

    init {
        val names = workerFactory.workerNames
        names.forEach { taskId -> ensureRegistered(taskId) }
        logger.debug(TAG, "Eagerly registered ${names.size} background task handlers")
    }

    override fun schedulePeriodic(request: PeriodicTaskRequest) {
        ensureRegistered(request.id)
        activeRequests[request.id] = request
        submitRequest(request)
    }

    override fun scheduleAndExecute(request: PeriodicTaskRequest) {
        schedulePeriodic(request)
        executeImmediately(request.id)
    }

    override fun cancel(id: String) {
        activeRequests.remove(id)
        scheduler.cancelTaskRequestWithIdentifier(id)
        logger.debug(TAG, "Cancelled task [$id]")
    }

    override fun cancelAll() {
        activeRequests.clear()
        scheduler.cancelAllTaskRequests()
        logger.debug(TAG, "Cancelled all tasks")
    }

    override fun rescheduleBackgroundTask() {
        activeRequests.values.forEach { request ->
            submitRequest(request)
        }
        logger.debug(TAG, "Rescheduled ${activeRequests.size} tasks")
    }

    private fun ensureRegistered(taskId: String) {
        if (taskId in registeredTaskIds) return
        scheduler.registerForTaskWithIdentifier(
            identifier = taskId,
            usingQueue = null,
            launchHandler = ::handleTask,
        )
        registeredTaskIds.add(taskId)
        logger.debug(TAG, "Registered background task [$taskId]")
    }

    /**
     * Submits a background task request to the system scheduler.
     *
     * @param request the periodic task configuration (id, interval, constraints).
     * @param useFullInterval controls the `earliestBeginDate` strategy:
     *  - `false` (default): uses a short 5-minute delay. Used for initial scheduling
     *    ([schedulePeriodic]) and background re-registration ([rescheduleBackgroundTask]) so
     *    the task becomes eligible soon. Without this, every app-open-close cycle would push
     *    the begin date forward by the full interval (e.g. 6 hours), preventing the task
     *    from ever becoming eligible.
     *  - `true`: uses the full [PeriodicTaskRequest.intervalMs]. Used only when re-scheduling
     *    after a task has actually executed ([handleTask]), establishing the real periodic cadence.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun submitRequest(request: PeriodicTaskRequest, useFullInterval: Boolean = false) {
        val intervalSeconds = if (useFullInterval) {
            request.intervalMs / 1000.0
        } else {
            INITIAL_DELAY_SECONDS
        }
        val earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(intervalSeconds)

        val bgRequest = if (request.longRunning) {
            BGProcessingTaskRequest(identifier = request.id).apply {
                this.earliestBeginDate = earliestBeginDate
                requiresNetworkConnectivity = request.constraints.requiresNetwork
                requiresExternalPower = false
            }
        } else {
            BGAppRefreshTaskRequest(identifier = request.id).apply {
                this.earliestBeginDate = earliestBeginDate
            }
        }

        try {
            scheduler.submitTaskRequest(taskRequest = bgRequest, error = null)
            val type = if (request.longRunning) "processing" else "refresh"
            logger.debug(TAG, "Scheduled $type task [${request.id}] for $earliestBeginDate")
        } catch (t: Throwable) {
            logger.error(TAG, "Error scheduling task [${request.id}]: ${t.message}")
        }
    }

    private fun executeImmediately(taskId: String) {
        val worker = workerFactory.createWorker(taskId) ?: run {
            logger.error(TAG, "No worker found for [$taskId]")
            return
        }

        logger.debug(TAG, "Starting immediate execution of [$taskId]")
        appCoroutineScope.io.launch {
            try {
                worker.doWork()
                logger.debug(TAG, "Immediate execution of [$taskId] completed")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logger.error(TAG, "Immediate execution of [$taskId] failed: ${e.message}")
            }
        }
    }

    private fun handleTask(bgTask: BGTask?) {
        val taskId = bgTask?.identifier ?: return
        val request = activeRequests[taskId]

        val worker = workerFactory.createWorker(taskId) ?: run {
            logger.error(TAG, "Received unknown task [$taskId]")
            bgTask.setTaskCompletedWithSuccess(false)
            return
        }

        executeWithinWindow(bgTask, worker)

        if (request != null) {
            submitRequest(request, useFullInterval = true)
        }
    }

    private fun executeWithinWindow(bgTask: BGTask, worker: BackgroundWorker) {
        val taskId = bgTask.identifier
        logger.debug(TAG, "Starting task [$taskId]")

        val taskCompleted = atomic(false)

        fun completeTask(success: Boolean) {
            if (taskCompleted.compareAndSet(expect = false, update = true)) {
                bgTask.setTaskCompletedWithSuccess(success)
            }
        }

        bgTask.expirationHandler = {
            logger.debug(TAG, "Task [$taskId] expired, cancelling")
            completeTask(false)
        }

        appCoroutineScope.io.launch {
            try {
                val result = worker.doWork()
                val success = result is WorkerResult.Success
                completeTask(success)
                logger.debug(TAG, "Task [$taskId] completed with result: $result")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                completeTask(false)
                logger.error(TAG, "Task [$taskId] failed: ${e.message}")
            }
        }
    }

    private companion object {
        private const val TAG = "IosTaskScheduler"
        private const val INITIAL_DELAY_SECONDS = 300.0
    }
}
