package com.thomaskioko.tvmaniac.core.networkutil

import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun observeConnectionState(): Flow<ConnectionState>
}

sealed class ConnectionState {
    object ConnectionAvailable : ConnectionState()
    object NoConnection : ConnectionState()
}
