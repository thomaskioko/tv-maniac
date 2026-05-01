package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.connectivity.implementation.PlatformInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [PlatformInternetConnectionChecker::class],
)
public object TestConnectivityBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideInternetConnectionChecker(): InternetConnectionChecker =
        FakeInternetConnectionChecker(connected = true)
}
