package com.thomaskioko.tvmaniac.accountmanager.api

import kotlinx.coroutines.flow.Flow

public interface AccountManager {
    public val activeProvider: Flow<AccountProvider?>
    public val isConnected: Flow<Boolean>
    public val connectionEvents: Flow<AccountProvider>
    public val accounts: Flow<List<ConnectedAccount>>
    public val activeAccount: Flow<ConnectedAccount?>
    public val authError: Flow<AuthError?>
    public val activeAuthState: Flow<AuthState?>

    public fun getActiveProvider(): AccountProvider?

    public suspend fun logout(provider: AccountProvider)

    public suspend fun setActive(provider: AccountProvider)

    public suspend fun setAuthError(error: AuthError?)

    public suspend fun refreshActiveTokens(): TokenRefreshResult
}
