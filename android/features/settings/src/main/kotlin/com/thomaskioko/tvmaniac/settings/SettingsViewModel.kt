package com.thomaskioko.tvmaniac.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsViewModel(
    private val stateMachine: SettingsStateMachine,
    private val traktAuthManager: TraktAuthManager,
) : ViewModel(), TraktAuthManager by traktAuthManager {

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

}
