package com.thomaskioko.tvmaniac.traktauth

import android.content.SharedPreferences
import androidx.core.content.edit
import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.traktauth.model.TraktAuthState
import com.thomaskioko.tvmaniac.workmanager.ShowTasks
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthState

@OptIn(DelicateCoroutinesApi::class)
@Inject
class TraktAuthRepository(
    private val showTasks: ShowTasks,
    private val dispatchers: AppCoroutineDispatchers,
    private val authPrefs: SharedPreferences,
) {
    private val authState = MutableStateFlow(EmptyAuthState)

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)
    val state: StateFlow<TraktAuthState>
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

    private fun updateAuthState(authState: AuthState) {
        if (authState.isAuthorized) {
            _state.value = TraktAuthState.LOGGED_IN
        } else {
            _state.value = TraktAuthState.LOGGED_OUT
        }
    }

    fun clearAuth() {
        authState.value = EmptyAuthState
        clearPersistedAuthState()
    }

    fun onNewAuthState(newState: AuthState) {
        GlobalScope.launch(dispatchers.main) {
            authState.value = newState
        }
        GlobalScope.launch(dispatchers.io) {
            persistAuthState(newState)
        }

        showTasks.syncTraktFollowedShows()
    }

    private fun readAuthState(): AuthState {
        val stateJson = authPrefs.getString(PreferenceAuthKey, null)
        return when {
            stateJson != null -> AuthState.jsonDeserialize(stateJson)
            else -> AuthState()
        }
    }

    private fun persistAuthState(state: AuthState) {
        authPrefs.edit(commit = true) {
            putString(PreferenceAuthKey, state.jsonSerializeString())
        }
    }

    private fun clearPersistedAuthState() {
        authPrefs.edit(commit = true) {
            remove(PreferenceAuthKey)
        }
    }

    companion object {
        private val EmptyAuthState = AuthState()
        private const val PreferenceAuthKey = "stateJson"
    }
}
