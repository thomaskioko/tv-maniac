package com.thomaskioko.tvmaniac.core.connectivity.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public actual class PlatformInternetConnectionChecker : InternetConnectionChecker {
    public actual override fun isConnected(): Boolean = true
}
