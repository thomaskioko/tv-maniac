package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.di.MainCoroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTraktAuthRepository(
    private val datastoreRepository: DatastoreRepository,
    private val dispatchers: AppCoroutineDispatchers,
    @IoCoroutineScope private val ioScope: CoroutineScope,
    @MainCoroutineScope private val mainScope: CoroutineScope,
) : TraktAuthRepository {
    private val authState = MutableStateFlow(AuthState())

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)

    override fun observeState(): StateFlow<TraktAuthState> = _state.asStateFlow()

    init {
        ioScope.launch {
            authState.collect { authState -> updateAuthState(authState) }
        }

        mainScope.launch {
            val state = withContext(dispatchers.io) { datastoreRepository.getAuthState() }
            authState.value = state ?: AuthState()
        }
    }

    override fun clearAuth() {
        updateAuthState(AuthState())
        ioScope.launch { datastoreRepository.clearAuthState() }
    }

    override fun onNewAuthState(newState: AuthState) {
        ioScope.launch {
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
