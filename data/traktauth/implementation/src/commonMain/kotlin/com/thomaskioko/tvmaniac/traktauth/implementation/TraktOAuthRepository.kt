package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import com.thomaskioko.tvmaniac.oauth.api.OAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class TraktOAuthRepository(
    private val authStateHolder: AuthStateHolder,
    private val refreshAction: Lazy<DefaultTraktRefreshTokenAction>,
) : OAuthRepository {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override val state: Flow<AccountAuthState> = authStateHolder.state(SyncProviderSource.TRAKT)

    override val authState: Flow<AuthState?> = authStateHolder.authState(SyncProviderSource.TRAKT)

    override val authError: Flow<AuthError?> = authStateHolder.authError(SyncProviderSource.TRAKT)

    override val loginEvents: SharedFlow<Unit> = authStateHolder.loginEvents(SyncProviderSource.TRAKT)

    override fun isLoggedIn(): Boolean = authStateHolder.isLoggedIn(SyncProviderSource.TRAKT)

    override suspend fun getAuthState(): AuthState? = authStateHolder.getAuthState(SyncProviderSource.TRAKT)

    override suspend fun refreshTokens(): TokenRefreshResult =
        authStateHolder.refreshTokens(SyncProviderSource.TRAKT, refreshAction.value)

    override suspend fun logout() {
        authStateHolder.logout(SyncProviderSource.TRAKT)
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        authStateHolder.saveTokens(SyncProviderSource.TRAKT, accessToken, refreshToken, expiresAtSeconds)
    }

    override suspend fun setAuthError(error: AuthError?) {
        authStateHolder.setAuthError(SyncProviderSource.TRAKT, error)
    }
}
