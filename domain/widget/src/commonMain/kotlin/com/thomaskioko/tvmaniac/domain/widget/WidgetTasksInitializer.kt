package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@Inject
public class WidgetTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val observeWidgetShowsInteractor: ObserveWidgetShowsInteractor,
    private val observeWidgetThemeInteractor: ObserveWidgetThemeInteractor,
    private val widgetPublishers: Set<WidgetPublisher>,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
) {

    public fun init() {
        if (widgetPublishers.isEmpty()) return

        publishOnChange()
        updateRefreshSchedule()
    }

    public fun updateRefreshSchedule() {
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
            combine(
                observeWidgetShowsInteractor.flow.onStart { observeWidgetShowsInteractor(Unit) },
                observeWidgetThemeInteractor.flow.onStart { observeWidgetThemeInteractor(Unit) },
                ::WidgetPayload,
            )
                .distinctUntilChanged()
                .collect { payload ->
                    try {
                        widgetPublishers
                            .filter { it.hasInstalledWidgets() }
                            .forEach { it.publish(payload.shows, payload.theme) }
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (throwable: Exception) {
                        logger.error(TAG, "Widget publish failed: ${throwable.message}")
                    }
                }
        }
    }

    private data class WidgetPayload(
        val shows: List<WidgetShow>,
        val theme: AppTheme,
    )

    private companion object {
        private const val TAG = "WidgetTasksInitializer"
    }
}
