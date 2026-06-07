package com.thomaskioko.tvmaniac.traktauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

public interface TraktAuthRepository {

    public val state: Flow<AccountAuthState>

    public val authState: Flow<AuthState?>

    public fun isLoggedIn(): Boolean

    public val authError: Flow<AuthError?>

    /**
     * Emits exactly once per explicit sign-in completion (the user finished
     * the OAuth handshake and we just persisted fresh tokens). Cache restore
     * on app launch and token refresh paths do not emit, so cold relaunches
     * never re-trigger the post-login sync block.
     *
     * Backed by a `MutableSharedFlow` with `replay = 1` so a subscriber that
     * attaches after the emit still observes the sign-in (matters for tests
     * and for any future subscriber added late within the same process). The
     * flow lives in `AppScope` (process lifetime), so a cold relaunch starts
     * with an empty buffer and never replays a previous session's sign-in.
     */
    public val loginEvents: SharedFlow<Unit>

    public suspend fun getAuthState(): AuthState?

    public suspend fun refreshTokens(): TokenRefreshResult

    public suspend fun logout()

    public suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    )

    public suspend fun setAuthError(error: AuthError?)
}
