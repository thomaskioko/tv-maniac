package com.thomaskioko.tvmaniac.connectedaccount.testing

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

public class FakeConnectedAccountRepository : ConnectedAccountRepository {

    private val _activeProvider = MutableStateFlow<ConnectedProvider?>(null)
    private val _connectionEvents = MutableSharedFlow<ConnectedProvider>(replay = 1, extraBufferCapacity = 1)

    public fun setActiveProvider(provider: ConnectedProvider?) {
        _activeProvider.value = provider
    }

    public fun emitConnection(provider: ConnectedProvider) {
        _connectionEvents.tryEmit(provider)
    }

    override val activeProvider: Flow<ConnectedProvider?> = _activeProvider
    override val isConnected: Flow<Boolean> = _activeProvider.map { it != null }
    override val connectionEvents: Flow<ConnectedProvider> = _connectionEvents.asSharedFlow()
    override fun activeProviderOrNull(): ConnectedProvider? = _activeProvider.value
}
