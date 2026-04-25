package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.traktauth.implementation.di.TokenRefreshInitializerBindingContainer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [TokenRefreshInitializerBindingContainer::class],
)
public object TestAuthBindingContainer
