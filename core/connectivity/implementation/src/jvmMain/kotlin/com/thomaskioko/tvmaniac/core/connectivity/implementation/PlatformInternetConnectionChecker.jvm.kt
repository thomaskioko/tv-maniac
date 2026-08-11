package com.thomaskioko.tvmaniac.core.connectivity.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public actual class PlatformInternetConnectionChecker : InternetConnectionChecker {

    private val connectionState = MutableStateFlow(true)

    public actual override fun isConnected(): Boolean = true

    public actual override fun observeConnection(): Flow<Boolean> = connectionState
}
