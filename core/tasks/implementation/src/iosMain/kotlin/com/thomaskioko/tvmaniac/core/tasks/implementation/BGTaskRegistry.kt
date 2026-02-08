package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTask
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskRegistry
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import platform.BackgroundTasks.BGTask
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class BGTaskRegistry(
    private val scheduler: BGTaskSchedulerWrapper,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : BackgroundTaskRegistry {
    private val registeredTasks = mutableMapOf<String, BackgroundTask>()

    override fun register(task: BackgroundTask) {
        registeredTasks[task.taskId] = task
        scheduler.register(task.taskId, ::handleTask)
    }

    override fun schedule(taskId: String) {
        val task = registeredTasks[taskId] ?: run {
            logger.error(TAG, "Cannot schedule unregistered task [$taskId]")
            return
        }
        scheduler.submit(taskId, task.interval)
    }

    override fun scheduleAndExecute(taskId: String) {
        schedule(taskId)

        val task = registeredTasks[taskId] ?: return
        val scope = CoroutineScope(dispatchers.io + SupervisorJob())
        scope.launch {
            try {
                task.execute()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logger.error(TAG, "Immediate execution of [$taskId] failed: ${e.message}")
            } finally {
                scope.cancel()
            }
        }
    }

    override fun cancel(taskId: String) {
        scheduler.cancel(taskId)
    }

    private fun handleTask(bgTask: BGTask?) {
        val taskId = bgTask?.identifier ?: return
        val task = registeredTasks[taskId] ?: run {
            logger.error(TAG, "Received unknown task [$taskId]")
            bgTask.setTaskCompletedWithSuccess(false)
            return
        }

        executeWithinWindow(bgTask, task)
        scheduler.submit(taskId, task.interval)
    }

    private fun executeWithinWindow(bgTask: BGTask, task: BackgroundTask) {
        logger.debug(TAG, "Starting task [${task.taskId}]")

        val taskCompleted = atomic(false)
        val taskScope = CoroutineScope(dispatchers.io + SupervisorJob())

        fun completeTask(success: Boolean) {
            if (taskCompleted.compareAndSet(expect = false, update = true)) {
                bgTask.setTaskCompletedWithSuccess(success)
                taskScope.cancel()
            }
        }

        bgTask.expirationHandler = {
            logger.debug(TAG, "Task [${task.taskId}] expired, cancelling")
            completeTask(false)
        }

        taskScope.launch {
            try {
                task.execute()
                completeTask(true)
                logger.debug(TAG, "Task [${task.taskId}] completed successfully")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                completeTask(false)
                logger.error(TAG, "Task [${task.taskId}] failed: ${e.message}")
            }
        }
    }

    private companion object {
        private const val TAG = "BGTaskRegistry"
    }
}
