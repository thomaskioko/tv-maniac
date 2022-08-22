package com.thomaskioko.tvmaniac.trakt.implementation

import android.content.SharedPreferences
import androidx.core.content.edit
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.TraktAuthState
import com.thomaskioko.tvmaniac.trakt.api.TraktManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import javax.inject.Named
import javax.inject.Singleton

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class TraktManagerImpl constructor(
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val mainDispatcher: CoroutineDispatcher,
    @Named("auth") private val authPrefs: SharedPreferences,
) : TraktManager {
    private val authState = MutableStateFlow(EmptyAuthState)

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)
    override val state: StateFlow<TraktAuthState>
        get() = _state.asStateFlow()

    init {
        GlobalScope.launch(ioDispatcher) {
            authState.collect { authState ->
                updateAuthState(authState)
            }
        }

        GlobalScope.launch(mainDispatcher) {
            val state = withContext(ioDispatcher) { readAuthState() }
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

    override fun clearAuth() {
        authState.value = EmptyAuthState
        clearPersistedAuthState()
    }

    override fun onNewAuthState(newState: AuthState) {
        GlobalScope.launch(mainDispatcher) {
            authState.value = newState
        }
        GlobalScope.launch(ioDispatcher) {
            persistAuthState(newState)
        }

        //TODO:: Trigger Sync Data.

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


