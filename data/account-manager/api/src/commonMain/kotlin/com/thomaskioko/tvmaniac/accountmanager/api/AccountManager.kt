package com.thomaskioko.tvmaniac.accountmanager.api

import kotlinx.coroutines.flow.Flow

public interface AccountManager {
    public val activeProvider: Flow<SyncProviderSource?>
    public val isConnected: Flow<Boolean>
    public val connectionEvents: Flow<SyncProviderSource>
    public val accounts: Flow<List<ConnectedAccount>>
    public val activeAccount: Flow<ConnectedAccount?>
    public val authError: Flow<AuthError?>
    public val activeAuthState: Flow<AuthState?>

    public fun getActiveProvider(): SyncProviderSource?

    public suspend fun logout(provider: SyncProviderSource)

    public suspend fun setActive(provider: SyncProviderSource)

    public suspend fun setAuthError(error: AuthError?)

    public suspend fun refreshActiveTokens(): TokenRefreshResult
}
