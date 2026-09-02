package com.thomaskioko.tvmaniac.ui.widget.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.ui.widget.WidgetPreviewInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object WidgetPreviewInitializerBindingContainer {
    @Provides
    @IntoSet
    @AsyncInitializers
    public fun provideWidgetPreviewInitializer(bind: WidgetPreviewInitializer): Initializer = Initializer { bind.init() }
}
