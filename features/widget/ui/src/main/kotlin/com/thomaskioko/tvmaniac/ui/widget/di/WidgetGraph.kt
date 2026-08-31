package com.thomaskioko.tvmaniac.ui.widget.di

import android.content.Context
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLinkUrls
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.domain.widget.ObserveWidgetShowsInteractor

public interface WidgetGraph {
    public val observeWidgetShowsInteractor: ObserveWidgetShowsInteractor
    public val backgroundTaskScheduler: BackgroundTaskScheduler
    public val deepLinkUrls: DeepLinkUrls
}

internal val Context.widgetGraph: WidgetGraph
    get() = applicationContext as WidgetGraph
