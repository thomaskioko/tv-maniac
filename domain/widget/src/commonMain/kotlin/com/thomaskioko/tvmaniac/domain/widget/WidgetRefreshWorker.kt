package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class WidgetRefreshWorker(
    private val observeWidgetShowsInteractor: ObserveWidgetShowsInteractor,
    private val widgetPublishers: Set<WidgetPublisher>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        if (widgetPublishers.none { it.hasInstalledWidgets() }) {
            logger.debug(TAG, "No widget installed, skipping refresh")
            return WorkerResult.Success
        }

        return try {
            val shows = observeWidgetShowsInteractor.flow
                .onStart { observeWidgetShowsInteractor(Unit) }
                .first()
            widgetPublishers.forEach { it.publish(shows) }
            WorkerResult.Success
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Exception) {
            logger.error(TAG, "Widget refresh failed: ${throwable.message}")
            WorkerResult.Retry(throwable.message)
        }
    }

    public companion object {
        public const val WORKER_NAME: String = "com.thomaskioko.tvmaniac.widgetrefresh"
        private const val TAG = "WidgetRefreshWorker"
        private const val SIX_HOURS_MS = 6L * 60 * 60 * 1000

        public val REQUEST: PeriodicTaskRequest = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = SIX_HOURS_MS,
        )
    }
}
