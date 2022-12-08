package com.thomaskioko.tvmaniac.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.settings.api.SettingsActions
import com.thomaskioko.tvmaniac.settings.api.SettingsContent
import com.thomaskioko.tvmaniac.settings.api.SettingsState
import com.thomaskioko.tvmaniac.settings.api.SettingsStateMachine
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.ObserveTraktUserInteractor
import com.thomaskioko.tvmaniac.traktauth.ObserveTraktAuthStateInteractor
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val stateMachine: SettingsStateMachine,
    private val traktAuthManager: TraktAuthManager,
    private val traktAuthInteractor: ObserveTraktAuthStateInteractor,
    private val observeTraktUserInteractor: ObserveTraktUserInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), TraktAuthManager by traktAuthManager {

    val state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsContent.EMPTY)

    init {
        viewModelScope.launch {
            stateMachine.state
                .collect {
                    state.value = it
                }
        }

        viewModelScope.launch {
            traktAuthInteractor.invoke(Unit)
                .collect {

                }
        }
    }

    fun dispatch(action: SettingsActions) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }

}
