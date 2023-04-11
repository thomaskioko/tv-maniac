package com.thomaskioko.tvmaniac.core.networkutil

import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    val connectivityState: ConnectionState
    fun observeConnectionState(): Flow<ConnectionState>
}

sealed class ConnectionState {
    object ConnectionAvailable : ConnectionState()
    object NoConnection : ConnectionState()
}
