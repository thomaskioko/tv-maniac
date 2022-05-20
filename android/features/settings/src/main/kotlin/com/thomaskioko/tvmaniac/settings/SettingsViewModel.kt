package com.thomaskioko.tvmaniac.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shared.persistance.SettingsActions
import com.thomaskioko.tvmaniac.shared.persistance.SettingsEffect
import com.thomaskioko.tvmaniac.shared.persistance.SettingsState
import com.thomaskioko.tvmaniac.shared.persistance.TvManiacPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreference: TvManiacPreferences,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : Store<SettingsState, SettingsActions, SettingsEffect>, ViewModel() {

    private val state = MutableStateFlow(SettingsState.DEFAULT)
    private val sideEffect = MutableSharedFlow<SettingsEffect>()

    init {
        dispatch(SettingsActions.LoadTheme)
    }

    override fun observeState(): StateFlow<SettingsState> = state

    override fun observeSideEffect(): Flow<SettingsEffect> = sideEffect

    override fun dispatch(action: SettingsActions) {
        when (action) {
            is SettingsActions.ThemeSelected -> {
                viewModelScope.launch(context = ioDispatcher) {
                    themePreference.emitTheme(action.theme)
                }
            }
            SettingsActions.ThemeClicked -> {
                viewModelScope.launch(context = ioDispatcher) {
                    val newState = state.value.copy(
                        showPopup = !state.value.showPopup
                    )
                    state.emit(newState)
                }
            }
            SettingsActions.LoadTheme -> {
                viewModelScope.launch {
                    themePreference.observeTheme()
                        .collect {
                            val newState = state.value.copy(
                                theme = it,
                            )
                            state.emit(newState)
                        }
                }
            }
        }
    }
}
