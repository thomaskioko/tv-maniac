package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.SyncTasks
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.followedshows.FollowedShowsSyncInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarMatchStrictly
import platform.Foundation.NSDate
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosSyncTasks(
    private val followedShowsSyncInteractor: Lazy<FollowedShowsSyncInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val dateTimeProvider: Lazy<DateTimeProvider>,
    private val scope: AppCoroutineScope,
    private val logger: Logger,
) : SyncTasks {
    private val taskScheduler by lazy { BGTaskScheduler.sharedScheduler }

    override fun setup() {
        taskScheduler.registerForTaskWithIdentifier(
            identifier = TASK_ID,
            usingQueue = null,
            launchHandler = ::handleTask,
        )
        logger.debug(TAG, "Registered library sync task [$TASK_ID]")
    }

    override fun scheduleLibrarySync() {
        scheduleTask()
    }

    override fun cancelLibrarySync() {
        taskScheduler.cancelTaskRequestWithIdentifier(TASK_ID)
        logger.debug(TAG, "Cancelled library sync task")
    }

    private fun handleTask(task: BGTask?) {
        if (task?.identifier != TASK_ID) return

        task.runTask { performSync() }
        scheduleTask()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun scheduleTask() {
        val request = BGAppRefreshTaskRequest(identifier = TASK_ID).apply {
            earliestBeginDate = nextSyncDate()
        }

        try {
            taskScheduler.submitTaskRequest(taskRequest = request, error = null)
            logger.debug(TAG, "Scheduled library sync for ${request.earliestBeginDate}")
        } catch (t: Throwable) {
            logger.error(TAG, "Error scheduling library sync: ${t.message}")
        }
    }

    private fun BGTask.runTask(block: suspend () -> Boolean) {
        logger.debug(TAG, "Starting library sync task")

        val job = scope.io.launch {
            try {
                block()
            } catch (e: kotlinx.coroutines.CancellationException) {
                logger.debug(TAG, "Sync cancelled: ${e.message}")
                throw e
            }
        }

        expirationHandler = {
            logger.debug(TAG, "Library sync task expired, cancelling job")
            job.cancel()
            setTaskCompletedWithSuccess(false)
        }

        runBlocking {
            try {
                job.join()
                setTaskCompletedWithSuccess(true)
                logger.debug(TAG, "Library sync task completed successfully")
            } catch (e: kotlinx.coroutines.CancellationException) {
                logger.debug(TAG, "Sync was cancelled: ${e.message}")
                setTaskCompletedWithSuccess(false)
            } catch (e: Throwable) {
                setTaskCompletedWithSuccess(false)
                logger.error(TAG, "Library sync task failed: ${e.message}")
            }
        }
    }

    private suspend fun performSync(): Boolean {
        if (traktAuthRepository.value.state.first() != TraktAuthState.LOGGED_IN) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return true
        }

        return try {
            followedShowsSyncInteractor.value.executeSync(
                FollowedShowsSyncInteractor.Param(forceRefresh = true),
            )
            datastoreRepository.value.setLastSyncTimestamp(dateTimeProvider.value.nowMillis())
            true
        } catch (e: Exception) {
            logger.error(TAG, "Library sync failed: ${e.message}")
            false
        }
    }

    private fun nextSyncDate(): NSDate {
        return NSCalendar.currentCalendar.nextDateAfterDate(
            date = NSDate(),
            matchingHour = 0,
            minute = 0,
            second = 0,
            options = NSCalendarMatchStrictly,
        )!!
    }

    private companion object {
        private const val TAG = "IosSyncTasks"
        private const val TASK_ID = "com.thomaskioko.tvmaniac.librarysync"
    }
}
