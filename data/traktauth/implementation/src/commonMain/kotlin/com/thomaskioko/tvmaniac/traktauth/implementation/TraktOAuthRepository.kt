package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
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

    override val provider: AccountProvider = AccountProvider.TRAKT

    override val state: Flow<AccountAuthState> = authStateHolder.state(AccountProvider.TRAKT)

    override val authState: Flow<AuthState?> = authStateHolder.authState(AccountProvider.TRAKT)

    override val authError: Flow<AuthError?> = authStateHolder.authError(AccountProvider.TRAKT)

    override val loginEvents: SharedFlow<Unit> = authStateHolder.loginEvents(AccountProvider.TRAKT)

    override fun isLoggedIn(): Boolean = authStateHolder.isLoggedIn(AccountProvider.TRAKT)

    override suspend fun getAuthState(): AuthState? = authStateHolder.getAuthState(AccountProvider.TRAKT)

    override suspend fun refreshTokens(): TokenRefreshResult =
        authStateHolder.refreshTokens(AccountProvider.TRAKT, refreshAction.value)

    override suspend fun logout() {
        authStateHolder.logout(AccountProvider.TRAKT)
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        authStateHolder.saveTokens(AccountProvider.TRAKT, accessToken, refreshToken, expiresAtSeconds)
    }

    override suspend fun setAuthError(error: AuthError?) {
        authStateHolder.setAuthError(AccountProvider.TRAKT, error)
    }
}
