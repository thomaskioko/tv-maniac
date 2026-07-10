package com.thomaskioko.tvmaniac.accountmanager.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

public class FakeAccountManager : AccountManager {

    private val _activeProvider = MutableStateFlow<SyncProviderSource?>(null)
    private val _connectionEvents = MutableSharedFlow<SyncProviderSource>(replay = 1, extraBufferCapacity = 1)
    private val _accounts = MutableStateFlow<List<ConnectedAccount>>(emptyList())
    private val _authError = MutableStateFlow<AuthError?>(null)
    private val _activeAuthState = MutableStateFlow<AuthState?>(null)
    private var refreshOutcome: TokenRefreshResult = TokenRefreshResult.NotLoggedIn

    public var lastLogoutProvider: SyncProviderSource? = null
        private set

    public fun setActiveProvider(provider: SyncProviderSource?) {
        _activeProvider.value = provider
    }

    public fun emitConnection(provider: SyncProviderSource) {
        _connectionEvents.tryEmit(provider)
    }

    public fun setAccounts(accounts: List<ConnectedAccount>) {
        _accounts.value = accounts
    }

    public fun setAuthErrorValue(error: AuthError?) {
        _authError.value = error
    }

    public fun setActiveAuthState(authState: AuthState?) {
        _activeAuthState.value = authState
    }

    public fun setRefreshOutcome(outcome: TokenRefreshResult) {
        refreshOutcome = outcome
    }

    override val activeProvider: Flow<SyncProviderSource?> = _activeProvider
    override val isConnected: Flow<Boolean> = _activeProvider.map { it != null }
    override val connectionEvents: Flow<SyncProviderSource> = _connectionEvents.asSharedFlow()
    override val accounts: Flow<List<ConnectedAccount>> = _accounts
    override val activeAccount: Flow<ConnectedAccount?> = _accounts.map { list -> list.firstOrNull { it.isActive } }
    override val authError: Flow<AuthError?> = _authError
    override val activeAuthState: Flow<AuthState?> = _activeAuthState

    override fun getActiveProvider(): SyncProviderSource? = _activeProvider.value

    override suspend fun logout(provider: SyncProviderSource) {
        lastLogoutProvider = provider
        if (_activeProvider.value == provider) {
            _activeProvider.value = null
        }
    }

    override suspend fun setActive(provider: SyncProviderSource) {
        _activeProvider.value = provider
    }

    override suspend fun setAuthError(error: AuthError?) {
        _authError.value = error
    }

    override suspend fun refreshActiveTokens(): TokenRefreshResult = refreshOutcome
}
