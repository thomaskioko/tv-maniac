package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
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

    @OptIn(ExperimentalForeignApi::class)
    private fun submitRequest(request: PeriodicTaskRequest) {
        val intervalSeconds = request.intervalMs / 1000.0
        val bgRequest = BGAppRefreshTaskRequest(identifier = request.id).apply {
            earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(intervalSeconds)
        }

        try {
            scheduler.submitTaskRequest(taskRequest = bgRequest, error = null)
            logger.debug(TAG, "Scheduled task [${request.id}] for ${bgRequest.earliestBeginDate}")
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
            submitRequest(request)
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
    }
}
