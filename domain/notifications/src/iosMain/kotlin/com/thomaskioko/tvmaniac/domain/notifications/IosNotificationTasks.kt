package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTask
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskRegistry
import com.thomaskioko.tvmaniac.domain.notifications.interactor.RefreshUpcomingSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncTraktCalendarInteractor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration.Companion.hours

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = NotificationTasks::class)
@ContributesBinding(AppScope::class, boundType = BackgroundTask::class, multibinding = true)
public class IosNotificationTasks(
    private val registry: BackgroundTaskRegistry,
    private val syncTraktCalendarInteractor: Lazy<SyncTraktCalendarInteractor>,
    private val refreshUpcomingSeasonDetailsInteractor: Lazy<RefreshUpcomingSeasonDetailsInteractor>,
    private val scheduleEpisodeNotificationsInteractor: Lazy<ScheduleEpisodeNotificationsInteractor>,
    private val logger: Logger,
) : NotificationTasks, BackgroundTask {

    override val taskId: String = TASK_ID
    override val interval: Double = CHECK_INTERVAL_SECONDS

    override fun setup() {
        registry.register(this)
    }

    override fun scheduleEpisodeNotifications() {
        registry.schedule(taskId)
    }

    override fun scheduleAndRunEpisodeNotifications() {
        registry.scheduleAndExecute(taskId)
    }

    override fun cancelEpisodeNotifications() {
        registry.cancel(taskId)
    }

    override fun rescheduleBackgroundTask() {
        registry.schedule(taskId)
    }

    override suspend fun execute() {
        logger.debug(TAG, "Episode notification task running (lookahead: $lookaheadLimit)")

        runCatching {
            refreshUpcomingSeasonDetailsInteractor.value.executeSync(refreshParams)
        }
            .onSuccess { logger.debug(TAG, "Season details refresh completed") }
            .onFailure { logger.error(TAG, "Season details refresh failed: ${it.message}") }

        runCatching {
            syncTraktCalendarInteractor.value.executeSync(
                SyncTraktCalendarInteractor.Params(forceRefresh = true),
            )
        }
            .onSuccess { logger.debug(TAG, "Calendar sync completed") }
            .onFailure { logger.error(TAG, "Calendar sync failed: ${it.message}") }

        runCatching {
            scheduleEpisodeNotificationsInteractor.value.executeSync(scheduleParams)
        }
            .onSuccess { logger.debug(TAG, "Notification scheduling completed") }
            .onFailure { logger.error(TAG, "Notification scheduling failed: ${it.message}") }

        logger.debug(TAG, "Episode notification task finished")
    }

    private companion object {
        private const val TAG = "IosNotificationTasks"
        private const val TASK_ID = "com.thomaskioko.tvmaniac.episodenotifications"
        private const val CHECK_INTERVAL_SECONDS = 6.0 * 60 * 60 // Task runs every 6 hours
        private val NOTIFICATION_CHECK_INTERVAL = 6.hours
        private const val LOOKAHEAD_MULTIPLIER = 4.0 // 24-hour lookahead window, safe due to iOS dedup by identifier
        private val lookaheadLimit = NOTIFICATION_CHECK_INTERVAL * LOOKAHEAD_MULTIPLIER
        private val refreshParams = RefreshUpcomingSeasonDetailsInteractor.Params()
        private val scheduleParams = ScheduleEpisodeNotificationsInteractor.Params(
            limit = lookaheadLimit,
        )
    }
}
