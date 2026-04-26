package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object TestInitializerBindingContainer {

    @Provides
    @IntoSet
    @Initializers
    public fun provideNoOpInitializer(): () -> Unit = {}

    @Provides
    @IntoSet
    @AsyncInitializers
    public fun provideNoOpAsyncInitializer(): () -> Unit = {}
}
