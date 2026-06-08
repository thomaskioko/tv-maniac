package com.thomaskioko.tvmaniac.accountmanager.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Auth state and account-level actions for a single [AccountProvider]. Each backend contributes one
 * implementation into a multibound set; [AccountManager] resolves the active one.
 */
public interface AccountAuthRepository : ProviderScoped {
    public val state: Flow<AccountAuthState>
    public val authState: Flow<AuthState?>
    public val authError: Flow<AuthError?>

    /** Emits once per explicit sign-in completion. Cache restore and token refresh do not emit. */
    public val loginEvents: SharedFlow<Unit>

    public fun isLoggedIn(): Boolean

    public suspend fun logout()

    public suspend fun refreshTokens(): TokenRefreshResult

    public suspend fun setAuthError(error: AuthError?)
}
