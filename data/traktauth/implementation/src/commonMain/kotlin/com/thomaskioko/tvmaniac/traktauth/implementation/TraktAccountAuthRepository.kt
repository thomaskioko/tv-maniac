package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.oauth.api.OAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@SingleIn(AppScope::class)
@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@AccountProviderKey(AccountProvider.TRAKT) AccountAuthRepository>(),
)
public class TraktAccountAuthRepository(
    private val traktAuthRepository: OAuthRepository,
) : AccountAuthRepository {

    override val provider: AccountProvider = AccountProvider.TRAKT

    override val state: Flow<AccountAuthState> = traktAuthRepository.state

    override val authState: Flow<AuthState?> = traktAuthRepository.authState

    override val authError: Flow<AuthError?> = traktAuthRepository.authError

    override val loginEvents: SharedFlow<Unit> = traktAuthRepository.loginEvents

    override fun isLoggedIn(): Boolean = traktAuthRepository.isLoggedIn()

    override suspend fun logout() {
        traktAuthRepository.logout()
    }

    override suspend fun refreshTokens(): TokenRefreshResult = traktAuthRepository.refreshTokens()

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        traktAuthRepository.saveTokens(accessToken, refreshToken, expiresAtSeconds)
    }

    override suspend fun setAuthError(error: AuthError?) {
        traktAuthRepository.setAuthError(error)
    }
}
