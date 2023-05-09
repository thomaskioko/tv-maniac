package com.thomaskioko.tvmaniac.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.settings.SettingsActions
import com.thomaskioko.tvmaniac.presentation.settings.SettingsContent
import com.thomaskioko.tvmaniac.presentation.settings.SettingsState
import com.thomaskioko.tvmaniac.presentation.settings.SettingsStateMachine
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsViewModel(
    private val stateMachine: SettingsStateMachine,
    private val traktAuthManager: TraktAuthManager,
) : ViewModel() {

    val state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsContent.EMPTY)

    init {
        viewModelScope.launch {
            stateMachine.state
                .collect {
                    state.value = it
                }
        }
    }

    fun dispatch(action: SettingsActions) {
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
