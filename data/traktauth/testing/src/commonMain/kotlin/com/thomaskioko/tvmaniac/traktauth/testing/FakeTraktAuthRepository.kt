package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.implementation.DefaultTraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultTraktAuthRepository::class])
public class FakeTraktAuthRepository : TraktAuthRepository {

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)
    private val _authState = MutableStateFlow<AuthState?>(null)
    private var refreshOutcome: TokenRefreshResult = TokenRefreshResult.NotLoggedIn
    private val _authError = MutableStateFlow<AuthError?>(null)

    public suspend fun setState(traktAuthState: TraktAuthState) {
        _state.emit(traktAuthState)
    }

    public fun setAuthState(state: AuthState?) {
        _authState.value = state
    }

    public fun setRefreshOutcome(outcome: TokenRefreshResult) {
        refreshOutcome = outcome
    }

    override val state: Flow<TraktAuthState> = _state.asStateFlow()

    override val authState: Flow<AuthState?> = _authState.asStateFlow()

    override val authError: Flow<AuthError?> = _authError.asStateFlow()

    override fun isLoggedIn(): Boolean = _state.value == TraktAuthState.LOGGED_IN

    override suspend fun getAuthState(): AuthState? = _authState.value

    override suspend fun refreshTokens(): TokenRefreshResult = refreshOutcome

    override suspend fun logout() {
        _state.emit(TraktAuthState.LOGGED_OUT)
        _authState.value = null
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        _state.emit(TraktAuthState.LOGGED_IN)
    }

    override suspend fun setAuthError(error: AuthError?) {
        _authError.emit(error)
    }
}
