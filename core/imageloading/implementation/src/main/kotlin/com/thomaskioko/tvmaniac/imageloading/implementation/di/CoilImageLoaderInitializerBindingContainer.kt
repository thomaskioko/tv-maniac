package com.thomaskioko.tvmaniac.imageloading.implementation.di

import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import com.thomaskioko.tvmaniac.imageloading.implementation.CoilImageLoaderInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object CoilImageLoaderInitializerBindingContainer {
    @Provides
    @IntoSet
    @Initializers
    public fun provideCoilImageLoaderInitializer(bind: CoilImageLoaderInitializer): Initializer = Initializer { bind.init() }
}
