package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktAccountAuthRepository(
    private val traktAuthRepository: TraktAuthRepository,
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

    override suspend fun setAuthError(error: AuthError?) {
        traktAuthRepository.setAuthError(error)
    }
}
