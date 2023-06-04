package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
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
    private val datastoreRepository: DatastoreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktAuthRepository {
    private val authState = MutableStateFlow(AuthState())

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
            val state = withContext(dispatchers.io) {
                datastoreRepository.getAuthState()
            }
            authState.value = state
        }
    }

    override fun clearAuth() {
        updateAuthState(AuthState())
        GlobalScope.launch(dispatchers.io) { datastoreRepository.clearAuthState() }
    }

    override fun onNewAuthState(newState: AuthState) {
        GlobalScope.launch(dispatchers.io) {
            datastoreRepository.saveAuthState(newState)
            authState.value = newState
            updateAuthState(newState)
        }
    }

    override fun updateAuthState(authState: AuthState) {
        _state.value = when {
            authState.isAuthorized -> TraktAuthState.LOGGED_IN
            else -> TraktAuthState.LOGGED_OUT
        }
    }
}
