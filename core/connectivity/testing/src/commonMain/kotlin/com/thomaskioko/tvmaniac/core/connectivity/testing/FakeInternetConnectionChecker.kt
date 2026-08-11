package com.thomaskioko.tvmaniac.core.connectivity.testing

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeInternetConnectionChecker(
    connected: Boolean = true,
) : InternetConnectionChecker {

    private val connectionState = MutableStateFlow(connected)

    override fun isConnected(): Boolean = connectionState.value

    override fun observeConnection(): Flow<Boolean> = connectionState

    public fun setConnected(value: Boolean) {
        connectionState.value = value
    }
}
