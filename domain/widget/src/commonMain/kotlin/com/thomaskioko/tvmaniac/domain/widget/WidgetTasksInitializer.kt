package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@Inject
public class WidgetTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val observeWidgetShowsInteractor: ObserveWidgetShowsInteractor,
    private val widgetPublishers: Set<WidgetPublisher>,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
) {

    public fun init() {
        if (widgetPublishers.isEmpty()) return

        publishOnChange()
        updateRefreshSchedule()
    }

    private fun updateRefreshSchedule() {
        coroutineScope.launch {
            if (widgetPublishers.any { it.hasInstalledWidgets() }) {
                scheduler.schedulePeriodic(WidgetRefreshWorker.REQUEST)
            } else {
                scheduler.cancel(WidgetRefreshWorker.WORKER_NAME)
            }
        }
    }

    private fun publishOnChange() {
        coroutineScope.launch {
            observeWidgetShowsInteractor.flow
                .onStart { observeWidgetShowsInteractor(Unit) }
                .distinctUntilChanged()
                .collect { shows ->
                    try {
                        widgetPublishers
                            .filter { it.hasInstalledWidgets() }
                            .forEach { it.publish(shows) }
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (throwable: Exception) {
                        logger.error(TAG, "Widget publish failed: ${throwable.message}")
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "WidgetTasksInitializer"
    }
}
