package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.thomaskioko.tvmaniac.domain.widget.WidgetRefreshWorker
import com.thomaskioko.tvmaniac.ui.widget.di.widgetGraph

public class UpNextWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = UpNextWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        context.widgetGraph.backgroundTaskScheduler.schedulePeriodic(WidgetRefreshWorker.REQUEST)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        context.widgetGraph.backgroundTaskScheduler.cancel(WidgetRefreshWorker.WORKER_NAME)
    }
}
