package com.thomaskioko.tvmaniac.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.setWidgetPreviews
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
public class WidgetPreviewInitializer(
    @ApplicationContext private val context: Context,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
) {

    public fun init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return
        publishPreviews()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun publishPreviews() {
        coroutineScope.launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                if (!hasPublishedPreview(UpNextWidgetReceiver::class.java)) {
                    report(manager.setWidgetPreviews<UpNextWidgetReceiver>())
                }
                if (!hasPublishedPreview(UpNextPosterWidgetReceiver::class.java)) {
                    report(manager.setWidgetPreviews<UpNextPosterWidgetReceiver>())
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Exception) {
                logger.error(TAG, "Widget preview failed: ${throwable.message}")
            }
        }
    }

    private fun report(result: Int) {
        if (result == GlanceAppWidgetManager.SET_WIDGET_PREVIEWS_RESULT_RATE_LIMITED) {
            logger.debug(TAG, "Widget preview not published, rate limited")
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun hasPublishedPreview(receiver: Class<out GlanceAppWidgetReceiver>): Boolean {
        val provider = ComponentName(context, receiver)
        val info = AppWidgetManager.getInstance(context)
            .installedProviders
            .firstOrNull { it.provider == provider }
            ?: return false
        return info.generatedPreviewCategories and AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN != 0
    }

    private companion object {
        private const val TAG = "WidgetPreviewInitializer"
    }
}
