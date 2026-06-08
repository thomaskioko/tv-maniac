package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Shared, provider-keyed auth-state engine: caches and persists tokens per [AccountProvider] through
 * [AuthStore] and orchestrates refresh. Provider repositories forward to it, so the token-state logic
 * lives in one place rather than being duplicated per provider. [refreshTokens] takes the provider's
 * [TokenRefreshAction] (null for providers whose tokens never expire).
 */
public interface AuthStateHolder {
    public fun state(provider: AccountProvider): Flow<AccountAuthState>
    public fun authState(provider: AccountProvider): Flow<AuthState?>
    public fun authError(provider: AccountProvider): Flow<AuthError?>
    public fun loginEvents(provider: AccountProvider): SharedFlow<Unit>
    public fun isLoggedIn(provider: AccountProvider): Boolean
    public suspend fun getAuthState(provider: AccountProvider): AuthState?
    public suspend fun saveTokens(
        provider: AccountProvider,
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    )
    public suspend fun refreshTokens(provider: AccountProvider, action: TokenRefreshAction?): TokenRefreshResult
    public suspend fun logout(provider: AccountProvider)
    public suspend fun setAuthError(provider: AccountProvider, error: AuthError?)
}
