package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
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
@ContributesBinding(AppScope::class, boundType = BackgroundWorker::class, multibinding = true)
public class EpisodeNotificationWorker(
    private val syncTraktCalendarInteractor: Lazy<SyncTraktCalendarInteractor>,
    private val refreshInteractor: Lazy<RefreshUpcomingSeasonDetailsInteractor>,
    private val scheduleInteractor: Lazy<ScheduleEpisodeNotificationsInteractor>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Episode notification worker running")

        runCatching {
            refreshInteractor.value.executeSync(
                RefreshUpcomingSeasonDetailsInteractor.Params(),
            )
        }.onFailure { logger.error(TAG, "Season details refresh failed: ${it.message}") }

        runCatching {
            syncTraktCalendarInteractor.value.executeSync(
                SyncTraktCalendarInteractor.Params(forceRefresh = true),
            )
        }.onFailure { logger.error(TAG, "Calendar sync failed: ${it.message}") }

        return runCatching {
            scheduleInteractor.value.executeSync(
                ScheduleEpisodeNotificationsInteractor.Params(limit = LOOKAHEAD_LIMIT),
            )
        }.fold(
            onSuccess = { WorkerResult.Success },
            onFailure = {
                logger.error(TAG, "Notification scheduling failed: ${it.message}")
                WorkerResult.Failure(it.message)
            },
        )
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.episodenotifications"
        private const val TAG = "EpisodeNotificationWorker"
        private val NOTIFICATION_CHECK_INTERVAL = 6.hours
        private const val LOOKAHEAD_MULTIPLIER = 4.0
        private val LOOKAHEAD_LIMIT = NOTIFICATION_CHECK_INTERVAL * LOOKAHEAD_MULTIPLIER
        private const val SIX_HOURS_MS = 6L * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = SIX_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
            longRunning = true,
        )
    }
}
