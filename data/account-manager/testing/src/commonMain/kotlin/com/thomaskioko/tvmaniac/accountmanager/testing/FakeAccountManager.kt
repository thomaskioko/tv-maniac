package com.thomaskioko.tvmaniac.accountmanager.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

public class FakeAccountManager : AccountManager {

    private val _activeProvider = MutableStateFlow<AccountProvider?>(null)
    private val _connectionEvents = MutableSharedFlow<AccountProvider>(replay = 1, extraBufferCapacity = 1)

    public fun setActiveProvider(provider: AccountProvider?) {
        _activeProvider.value = provider
    }

    public fun emitConnection(provider: AccountProvider) {
        _connectionEvents.tryEmit(provider)
    }

    override val activeProvider: Flow<AccountProvider?> = _activeProvider
    override val isConnected: Flow<Boolean> = _activeProvider.map { it != null }
    override val connectionEvents: Flow<AccountProvider> = _connectionEvents.asSharedFlow()
    override fun getActiveProvider(): AccountProvider? = _activeProvider.value
}
