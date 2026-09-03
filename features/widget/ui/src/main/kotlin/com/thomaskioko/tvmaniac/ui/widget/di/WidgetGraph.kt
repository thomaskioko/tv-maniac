package com.thomaskioko.tvmaniac.ui.widget.di

import android.content.Context
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLinkUrls
import com.thomaskioko.tvmaniac.domain.widget.ObserveWidgetShowsInteractor
import com.thomaskioko.tvmaniac.domain.widget.ObserveWidgetThemeInteractor
import com.thomaskioko.tvmaniac.domain.widget.WidgetTasksInitializer

public interface WidgetGraph {
    public val observeWidgetShowsInteractor: ObserveWidgetShowsInteractor
    public val observeWidgetThemeInteractor: ObserveWidgetThemeInteractor
    public val widgetTasksInitializer: WidgetTasksInitializer
    public val deepLinkUrls: DeepLinkUrls
}

internal val Context.widgetGraph: WidgetGraph
    get() = applicationContext as WidgetGraph
