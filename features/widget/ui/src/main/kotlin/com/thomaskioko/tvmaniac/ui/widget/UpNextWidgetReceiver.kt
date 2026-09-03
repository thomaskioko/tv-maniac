package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.thomaskioko.tvmaniac.ui.widget.di.widgetGraph

public class UpNextWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = UpNextWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        context.widgetGraph.widgetTasksInitializer.updateRefreshSchedule()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        context.widgetGraph.widgetTasksInitializer.updateRefreshSchedule()
    }
}
