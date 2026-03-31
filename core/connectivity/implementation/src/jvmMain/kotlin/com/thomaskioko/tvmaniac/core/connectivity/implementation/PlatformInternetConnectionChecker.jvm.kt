package com.thomaskioko.tvmaniac.core.connectivity.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public actual class PlatformInternetConnectionChecker : InternetConnectionChecker {
    public actual override fun isConnected(): Boolean = true
}
