package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.implementation.TaskResult.Failure
import com.thomaskioko.tvmaniac.core.tasks.implementation.TaskResult.Success
import kotlinx.cinterop.ExperimentalForeignApi
import me.tatarka.inject.annotations.Inject
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class BGTaskSchedulerWrapper(
    private val logger: Logger,
) {
    private val scheduler = BGTaskScheduler.sharedScheduler

    public fun register(taskId: String, handler: (BGTask?) -> Unit) {
        scheduler.registerForTaskWithIdentifier(
            identifier = taskId,
            usingQueue = null,
            launchHandler = handler,
        )
        logger.debug(TAG, "Registered background task [$taskId]")
    }

    @OptIn(ExperimentalForeignApi::class)
    public fun submit(taskId: String, interval: Double): TaskResult {
        val request = BGProcessingTaskRequest(identifier = taskId).apply {
            requiresNetworkConnectivity = true
            requiresExternalPower = false
            earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(interval)
        }

        return try {
            scheduler.submitTaskRequest(taskRequest = request, error = null)
            logger.debug(TAG, "Scheduled task [$taskId] for ${request.earliestBeginDate}")
            Success
        } catch (t: Throwable) {
            logger.error(TAG, "Error scheduling task [$taskId]: ${t.message}")
            Failure(t.message ?: "Unknown error")
        }
    }

    public fun cancel(taskId: String) {
        scheduler.cancelTaskRequestWithIdentifier(taskId)
        logger.debug(TAG, "Cancelled task [$taskId]")
    }

    private companion object {
        private const val TAG = "BGTaskSchedulerWrapper"
    }
}
