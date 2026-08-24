package com.thomaskioko.tvmaniac.domain.widget.di

import com.thomaskioko.tvmaniac.domain.widget.WidgetPublisher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface WidgetMultibindings {
    @Multibinds(allowEmpty = true)
    public fun widgetPublishers(): Set<WidgetPublisher>
}
