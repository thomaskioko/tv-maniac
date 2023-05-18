package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@OptIn(DelicateCoroutinesApi::class)
@Inject
class TraktAuthRepositoryImpl(
    private val dispatchers: AppCoroutineDispatchers,
) : TraktAuthRepository {
    private val authState = MutableStateFlow(EmptyAuthState)

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)
    override val state: StateFlow<TraktAuthState>
        get() = _state.asStateFlow()

    init {
        GlobalScope.launch(dispatchers.io) {
            authState.collect { authState ->
                updateAuthState(authState)
            }
        }

        GlobalScope.launch(dispatchers.main) {
            val state = withContext(dispatchers.io) { readAuthState() }
            authState.value = state
        }
    }

    override fun updateAuthState(authState: AuthState) {
        if (authState.isAuthorized) {
            _state.value = TraktAuthState.LOGGED_IN
        } else {
            _state.value = TraktAuthState.LOGGED_OUT
        }
    }

    override fun clearAuth() {
        authState.value = EmptyAuthState
        clearPersistedAuthState()
    }

    override fun onNewAuthState(newState: AuthState) {
        GlobalScope.launch(dispatchers.main) {
            authState.value = newState
        }
        GlobalScope.launch(dispatchers.io) {
            persistAuthState(newState)
        }
    }

    private fun readAuthState(): AuthState {
        // TODO:: Add implementation. #61
        return AuthState(
            accessToken = "",
            refreshToken = "",
            isAuthorized = false,
        )
    }

    private fun persistAuthState(state: AuthState) {
        // TODO:: Add implementation. #61
    }

    private fun clearPersistedAuthState() {
        // TODO:: Add implementation. #61
    }

    companion object {
        private val EmptyAuthState = AuthState(
            accessToken = "",
            refreshToken = "",
            isAuthorized = false,
        )
    }
}
