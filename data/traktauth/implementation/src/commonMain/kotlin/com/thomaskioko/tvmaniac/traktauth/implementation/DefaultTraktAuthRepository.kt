package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.AuthStore
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktLoginAction
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTraktAuthRepository(
    private val dispatchers: AppCoroutineDispatchers,
    private val authStore: AuthStore,
    private val loginAction: Lazy<TraktLoginAction>,
    private val refreshTokenAction: Lazy<TraktRefreshTokenAction>,
) : TraktAuthRepository {

    private val authState = MutableStateFlow<AuthState?>(null)

    override val state: Flow<TraktAuthState> = authState.map { state ->
        when (state?.isAuthorized) {
            true -> TraktAuthState.LOGGED_IN
            else -> TraktAuthState.LOGGED_OUT
        }
    }

    override suspend fun getAuthState(): AuthState? {
        return withContext(dispatchers.io) {
            authState.value ?: authStore.get()?.also { authState.value = it }
        }
    }

    override suspend fun login(): AuthState? {
        return loginAction.value().also { newState ->
            updateAuthState(newState ?: AuthState.Empty)
        }
    }

    override suspend fun refreshTokens(): AuthState? {
        return getAuthState()
            ?.let { currentState ->
                refreshTokenAction.value.invoke(currentState)
            }
            .also { newState ->
                updateAuthState(newState ?: AuthState.Empty)
            }
    }

    override suspend fun logout() {
        updateAuthState(AuthState.Empty)
    }

    private suspend fun updateAuthState(authState: AuthState) {
        withContext(dispatchers.io) {
            if (authState.isAuthorized) {
                authStore.save(authState)
            } else {
                authStore.clear()
            }
        }
        this.authState.value = authState
    }
}
