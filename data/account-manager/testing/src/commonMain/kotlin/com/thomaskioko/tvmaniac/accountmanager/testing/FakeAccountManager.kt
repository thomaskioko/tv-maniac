package com.thomaskioko.tvmaniac.accountmanager.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

public class FakeAccountManager : AccountManager {

    private val _activeProvider = MutableStateFlow<AccountProvider?>(null)
    private val _connectionEvents = MutableSharedFlow<AccountProvider>(replay = 1, extraBufferCapacity = 1)
    private val _accounts = MutableStateFlow<List<ConnectedAccount>>(emptyList())
    private val _authError = MutableStateFlow<AuthError?>(null)
    private var refreshOutcome: TokenRefreshResult = TokenRefreshResult.NotLoggedIn

    public var lastLogoutProvider: AccountProvider? = null
        private set

    public fun setActiveProvider(provider: AccountProvider?) {
        _activeProvider.value = provider
    }

    public fun emitConnection(provider: AccountProvider) {
        _connectionEvents.tryEmit(provider)
    }

    public fun setAccounts(accounts: List<ConnectedAccount>) {
        _accounts.value = accounts
    }

    public fun setAuthErrorValue(error: AuthError?) {
        _authError.value = error
    }

    public fun setRefreshOutcome(outcome: TokenRefreshResult) {
        refreshOutcome = outcome
    }

    override val activeProvider: Flow<AccountProvider?> = _activeProvider
    override val isConnected: Flow<Boolean> = _activeProvider.map { it != null }
    override val connectionEvents: Flow<AccountProvider> = _connectionEvents.asSharedFlow()
    override val accounts: Flow<List<ConnectedAccount>> = _accounts
    override val activeAccount: Flow<ConnectedAccount?> = _accounts.map { list -> list.firstOrNull { it.isActive } }
    override val authError: Flow<AuthError?> = _authError

    override fun getActiveProvider(): AccountProvider? = _activeProvider.value

    override suspend fun logout(provider: AccountProvider) {
        lastLogoutProvider = provider
        if (_activeProvider.value == provider) {
            _activeProvider.value = null
        }
    }

    override suspend fun setActive(provider: AccountProvider) {
        _activeProvider.value = provider
    }

    override suspend fun setAuthError(error: AuthError?) {
        _authError.value = error
    }

    override suspend fun refreshActiveTokens(): TokenRefreshResult = refreshOutcome
}
