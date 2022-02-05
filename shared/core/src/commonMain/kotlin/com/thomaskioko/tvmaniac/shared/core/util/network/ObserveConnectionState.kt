package com.thomaskioko.tvmaniac.shared.core.util.network

import com.thomaskioko.tvmaniac.shared.core.AppContext
import kotlinx.coroutines.flow.Flow

expect class ObserveConnectionState actual constructor(context: AppContext) {
    /**
     * Network Utility to observe availability or unavailability of Internet connection
     */
    fun observeConnectivityAsFlow(): Flow<ConnectionState>
}

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}
