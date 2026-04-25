package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.imageloading.implementation.di.CoilImageLoaderInitializerBindingContainer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [CoilImageLoaderInitializerBindingContainer::class],
)
public object TestImageLoaderBindingContainer
