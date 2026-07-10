package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Shared, provider-keyed auth-state engine: caches and persists tokens per [SyncProviderSource] through
 * [AuthStore] and orchestrates refresh. Provider repositories forward to it, so the token-state logic
 * lives in one place rather than being duplicated per provider. [refreshTokens] takes the provider's
 * [TokenRefreshAction] (null for providers whose tokens never expire).
 */
public interface AuthStateHolder {
    public fun state(provider: SyncProviderSource): Flow<AccountAuthState>
    public fun authState(provider: SyncProviderSource): Flow<AuthState?>
    public fun authError(provider: SyncProviderSource): Flow<AuthError?>
    public fun loginEvents(provider: SyncProviderSource): SharedFlow<Unit>
    public fun isLoggedIn(provider: SyncProviderSource): Boolean
    public suspend fun getAuthState(provider: SyncProviderSource): AuthState?
    public suspend fun saveTokens(
        provider: SyncProviderSource,
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    )
    public suspend fun refreshTokens(provider: SyncProviderSource, action: TokenRefreshAction?): TokenRefreshResult
    public suspend fun logout(provider: SyncProviderSource)
    public suspend fun setAuthError(provider: SyncProviderSource, error: AuthError?)
}
