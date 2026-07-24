package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.domain.notifications.interactor.RefreshUpcomingSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncCalendarInteractor
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class EpisodeNotificationWorker(
    private val syncCalendarInteractor: Lazy<SyncCalendarInteractor>,
    private val refreshInteractor: Lazy<RefreshUpcomingSeasonDetailsInteractor>,
    private val scheduleInteractor: Lazy<ScheduleEpisodeNotificationsInteractor>,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Episode notification worker running")

        runStep("Initial notification scheduling") {
            scheduleInteractor.value.executeSync(ScheduleEpisodeNotificationsInteractor.Params())
        }
        runStep("Season details refresh") {
            refreshInteractor.value.executeSync(RefreshUpcomingSeasonDetailsInteractor.Params())
        }
        runStep("Calendar sync") {
            syncCalendarInteractor.value.executeSync(SyncCalendarInteractor.Params(forceRefresh = true))
        }

        return try {
            scheduleInteractor.value.executeSync(ScheduleEpisodeNotificationsInteractor.Params())
            WorkerResult.Success
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Exception) {
            logger.error(TAG, "Notification scheduling failed: ${throwable.message}")
            syncObserver.log(SyncError.BackgroundSyncFailed(WORKER_NAME, throwable))
            WorkerResult.Failure(throwable.message)
        }
    }

    private suspend fun runStep(name: String, block: suspend () -> Unit) {
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Exception) {
            logger.error(TAG, "$name failed: ${throwable.message}")
        }
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.episodenotifications"
        private const val TAG = "EpisodeNotificationWorker"
        private const val SIX_HOURS_MS = 6L * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = SIX_HOURS_MS,
        )
    }
}
