package com.thomaskioko.tvmaniac.app.debug.di

import com.thomaskioko.tvmaniac.app.debug.DebugNotificationInitializer
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object DebugNotificationInitializerBindingContainer {
    @Provides
    @IntoSet
    @Initializers
    public fun provideDebugNotificationInitializer(bind: DebugNotificationInitializer): Initializer = Initializer { bind.init() }
}
