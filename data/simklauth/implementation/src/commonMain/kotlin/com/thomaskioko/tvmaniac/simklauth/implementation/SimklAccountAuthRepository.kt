package com.thomaskioko.tvmaniac.simklauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@SingleIn(AppScope::class)
@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<
        @AccountProviderKey(AccountProvider.SIMKL)
        AccountAuthRepository,
        >(),
)
public class SimklAccountAuthRepository(
    private val authStateHolder: AuthStateHolder,
) : AccountAuthRepository {

    override val provider: AccountProvider = AccountProvider.SIMKL

    override val state: Flow<AccountAuthState> = authStateHolder.state(AccountProvider.SIMKL)

    override val authState: Flow<AuthState?> = authStateHolder.authState(AccountProvider.SIMKL)

    override val authError: Flow<AuthError?> = authStateHolder.authError(AccountProvider.SIMKL)

    override val loginEvents: SharedFlow<Unit> = authStateHolder.loginEvents(AccountProvider.SIMKL)

    override fun isLoggedIn(): Boolean = authStateHolder.isLoggedIn(AccountProvider.SIMKL)

    override suspend fun logout() {
        authStateHolder.logout(AccountProvider.SIMKL)
    }

    override suspend fun refreshTokens(): TokenRefreshResult =
        authStateHolder.refreshTokens(AccountProvider.SIMKL, action = null)

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        authStateHolder.saveTokens(
            provider = AccountProvider.SIMKL,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAtSeconds = expiresAtSeconds,
        )
    }

    override suspend fun setAuthError(error: AuthError?) {
        authStateHolder.setAuthError(AccountProvider.SIMKL, error)
    }
}
