package com.thomaskioko.tvmaniac.iosframework.di

import com.thomaskioko.tvmaniac.core.files.api.JsonFileManager
import com.thomaskioko.tvmaniac.domain.widget.SnapshotWidgetPublisher
import com.thomaskioko.tvmaniac.domain.widget.WidgetManager
import com.thomaskioko.tvmaniac.domain.widget.WidgetPublisher
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
public object WidgetPublisherBindingContainer {

    @Provides
    @IntoSet
    @SingleIn(AppScope::class)
    public fun provideWidgetPublisher(
        widgetManager: WidgetManager,
        jsonFileManager: JsonFileManager,
        dateTimeProvider: DateTimeProvider,
    ): WidgetPublisher = SnapshotWidgetPublisher(
        widgetManager = widgetManager,
        jsonFileManager = jsonFileManager,
        dateTimeProvider = dateTimeProvider,
    )
}
