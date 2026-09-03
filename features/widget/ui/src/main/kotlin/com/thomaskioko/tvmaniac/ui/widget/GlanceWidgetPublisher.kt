package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.domain.widget.WidgetPublisher
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class GlanceWidgetPublisher(
    @ApplicationContext private val context: Context,
) : WidgetPublisher {

    override suspend fun hasInstalledWidgets(): Boolean {
        val manager = GlanceAppWidgetManager(context)
        return manager.getGlanceIds(UpNextWidget::class.java).isNotEmpty() ||
            manager.getGlanceIds(UpNextPosterWidget::class.java).isNotEmpty()
    }

    override suspend fun publish(shows: List<WidgetShow>, theme: AppTheme) {
        UpNextWidget().updateAll(context)
        UpNextPosterWidget().updateAll(context)
    }
}
