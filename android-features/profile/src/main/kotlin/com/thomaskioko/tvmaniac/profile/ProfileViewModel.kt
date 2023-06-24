package com.thomaskioko.tvmaniac.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.profile.LoggedInContent
import com.thomaskioko.tvmaniac.presentation.profile.ProfileActions
import com.thomaskioko.tvmaniac.presentation.profile.ProfileState
import com.thomaskioko.tvmaniac.presentation.profile.ProfileStateMachine
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileViewModel(
    private val stateMachine: ProfileStateMachine,
    private val traktAuthManager: TraktAuthManager,
) : ViewModel() {

    val state: MutableStateFlow<ProfileState> = MutableStateFlow(LoggedInContent())

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                state.value = newState
            }
        }
    }

    fun dispatch(action: ProfileActions) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun login() {
        viewModelScope.launch {
            traktAuthManager.launchWebView()
        }
    }
}
