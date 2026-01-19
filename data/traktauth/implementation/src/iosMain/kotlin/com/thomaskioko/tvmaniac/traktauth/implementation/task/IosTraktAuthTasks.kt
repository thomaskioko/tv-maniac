package com.thomaskioko.tvmaniac.traktauth.implementation.task

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult.Success
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthTasks
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
public class IosTraktAuthTasks(
    private val traktAuthRepository: TraktAuthRepository,
    private val scope: AppCoroutineScope,
    private val logger: Logger,
) : TraktAuthTasks {
    private val taskScheduler by lazy { BGTaskScheduler.sharedScheduler }

    override fun setup() {
        taskScheduler.registerForTaskWithIdentifier(
            identifier = TASK_ID,
            usingQueue = null,
            launchHandler = ::handleTask,
        )
        logger.debug(TAG, "Registered task [$TASK_ID]")
    }

    override fun scheduleTokenRefresh() {
        scheduleTask(TASK_ID)
    }

    override fun cancelTokenRefresh() {
        taskScheduler.cancelTaskRequestWithIdentifier(TASK_ID)
    }

    private fun handleTask(task: BGTask?) {
        if (task?.identifier != TASK_ID) return

        task.runTask { performRefresh() }
        scheduleTask(TASK_ID)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun scheduleTask(id: String) {
        val request = BGAppRefreshTaskRequest(identifier = id).apply {
            earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(REFRESH_INTERVAL_SECONDS)
        }

        try {
            taskScheduler.submitTaskRequest(taskRequest = request, error = null)
            logger.debug(TAG, "Scheduled task [$id]")
        } catch (t: Throwable) {
            logger.error(TAG, "Error scheduling task: ${t.message}")
        }
    }

    private fun BGTask.runTask(block: suspend () -> Boolean) {
        logger.debug(TAG, "Starting task [$identifier]")

        val job = scope.io.launch { block() }

        expirationHandler = {
            logger.debug(TAG, "Task [$identifier] expired")
            setTaskCompletedWithSuccess(false)
            job.cancel()
        }

        runBlocking {
            try {
                job.join()
                setTaskCompletedWithSuccess(true)
                logger.debug(TAG, "Task [$identifier] completed successfully")
            } catch (e: Throwable) {
                setTaskCompletedWithSuccess(false)
                logger.error(TAG, "Task [$identifier] failed: ${e.message}")
            }
        }
    }

    private suspend fun performRefresh(): Boolean {
        val authState = traktAuthRepository.getAuthState() ?: return true
        if (!authState.isExpiringSoon()) return true
        return traktAuthRepository.refreshTokens() is Success
    }

    public companion object {
        private const val TAG = "IosTraktAuthTasks"
        private const val TASK_ID = "com.thomaskioko.tvmaniac.tokenrefresh"
        private const val REFRESH_INTERVAL_SECONDS = 5.0 * 24.0 * 60.0 * 60.0 // 5 days
    }
}
