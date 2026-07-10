package com.thomaskioko.tvmaniac.simklauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
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
        @AccountProviderKey(SyncProviderSource.SIMKL)
        AccountAuthRepository,
        >(),
)
public class SimklAccountAuthRepository(
    private val authStateHolder: AuthStateHolder,
) : AccountAuthRepository {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    override val state: Flow<AccountAuthState> = authStateHolder.state(SyncProviderSource.SIMKL)

    override val authState: Flow<AuthState?> = authStateHolder.authState(SyncProviderSource.SIMKL)

    override val authError: Flow<AuthError?> = authStateHolder.authError(SyncProviderSource.SIMKL)

    override val loginEvents: SharedFlow<Unit> = authStateHolder.loginEvents(SyncProviderSource.SIMKL)

    override fun isLoggedIn(): Boolean = authStateHolder.isLoggedIn(SyncProviderSource.SIMKL)

    override suspend fun logout() {
        authStateHolder.logout(SyncProviderSource.SIMKL)
    }

    override suspend fun refreshTokens(): TokenRefreshResult =
        authStateHolder.refreshTokens(SyncProviderSource.SIMKL, action = null)

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        authStateHolder.saveTokens(
            provider = SyncProviderSource.SIMKL,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAtSeconds = expiresAtSeconds,
        )
    }

    override suspend fun setAuthError(error: AuthError?) {
        authStateHolder.setAuthError(SyncProviderSource.SIMKL, error)
    }
}
