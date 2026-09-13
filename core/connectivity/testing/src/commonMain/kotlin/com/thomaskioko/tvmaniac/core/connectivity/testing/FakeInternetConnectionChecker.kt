package com.thomaskioko.tvmaniac.core.connectivity.testing

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

public class FakeInternetConnectionChecker(
    connected: Boolean = true,
) : InternetConnectionChecker {

    private val connectionState = MutableStateFlow(connected)
    private val reconnections = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun isConnected(): Boolean = connectionState.value

    override fun observeConnection(): Flow<Boolean> = connectionState

    override fun observeReconnection(): Flow<Unit> = reconnections.asSharedFlow()

    public fun setConnected(value: Boolean) {
        val reconnected = !connectionState.value && value
        connectionState.value = value
        if (reconnected) reconnections.tryEmit(Unit)
    }
}
