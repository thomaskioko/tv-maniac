package com.thomaskioko.tvmaniac.domain.widget.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.domain.widget.WidgetTasksInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public interface WidgetTasksInitializerBindingContainer {
    public companion object {
        @Provides
        @IntoSet
        @AsyncInitializers
        public fun provideWidgetTasksInitializer(
            bind: WidgetTasksInitializer,
        ): Initializer = Initializer { bind.init() }
    }
}
