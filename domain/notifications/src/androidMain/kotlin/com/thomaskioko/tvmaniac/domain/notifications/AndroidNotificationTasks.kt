package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorkerScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.NetworkRequirement
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.domain.notifications.interactor.RefreshUpcomingSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncTraktCalendarInteractor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = NotificationTasks::class)
public class AndroidNotificationTasks(
    private val scheduler: BackgroundWorkerScheduler,
    private val syncTraktCalendarInteractor: Lazy<SyncTraktCalendarInteractor>,
    private val refreshInteractor: Lazy<RefreshUpcomingSeasonDetailsInteractor>,
    private val scheduleInteractor: Lazy<ScheduleEpisodeNotificationsInteractor>,
    private val logger: Logger,
) : NotificationTasks, BackgroundWorker {

    override val workerName: String = WORKER_NAME
    override val interval: Duration = NOTIFICATION_CHECK_INTERVAL
    override val constraints: WorkerConstraints = WorkerConstraints(NetworkRequirement.CONNECTED)

    override fun setup() {
        scheduler.register(this)
    }

    override fun scheduleEpisodeNotifications() {
        scheduler.scheduleAndExecute(workerName)
    }

    override fun cancelEpisodeNotifications() {
        scheduler.cancel(workerName)
    }

    override suspend fun execute(): WorkerResult {
        logger.debug(TAG, "Episode notification worker running")

        runCatching {
            syncTraktCalendarInteractor.value.executeSync(
                SyncTraktCalendarInteractor.Params(forceRefresh = true),
            )
        }.onFailure { logger.error(TAG, "Calendar sync failed: ${it.message}") }

        val lookaheadLimit = NOTIFICATION_CHECK_INTERVAL * LOOKAHEAD_MULTIPLIER

        runCatching {
            refreshInteractor.value.executeSync(
                RefreshUpcomingSeasonDetailsInteractor.Params(limit = lookaheadLimit),
            )
        }.onFailure { logger.error(TAG, "Season details refresh failed: ${it.message}") }

        return runCatching {
            scheduleInteractor.value.executeSync(
                ScheduleEpisodeNotificationsInteractor.Params(limit = lookaheadLimit),
            )
        }.fold(
            onSuccess = { WorkerResult.Success },
            onFailure = {
                logger.error(TAG, "Notification scheduling failed: ${it.message}")
                WorkerResult.Failure
            },
        )
    }

    private companion object {
        private const val TAG = "AndroidNotificationTasks"
        private const val WORKER_NAME = "episode_notification_worker"
        private val NOTIFICATION_CHECK_INTERVAL = 6.hours // Worker runs every 6 hours
        private const val LOOKAHEAD_MULTIPLIER = 1.5 // 9-hour lookahead window, 3-hour overlap
    }
}
