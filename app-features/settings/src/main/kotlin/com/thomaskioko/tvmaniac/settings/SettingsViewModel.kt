package com.thomaskioko.tvmaniac.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.settings.SettingsActions.LoadTheme
import com.thomaskioko.tvmaniac.settings.SettingsActions.ThemeClicked
import com.thomaskioko.tvmaniac.settings.SettingsActions.ThemeSelected
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
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
        dispatch(LoadTheme)
    }

    override fun observeState(): StateFlow<SettingsState> = state

    override fun observeSideEffect(): Flow<SettingsEffect> = sideEffect

    @OptIn(InternalCoroutinesApi::class)
    override fun dispatch(action: SettingsActions) {
        when (action) {
            is ThemeSelected -> {
                viewModelScope.launch(context = ioDispatcher) {
                    themePreference.emitTheme(action.theme)
                }
            }
            ThemeClicked -> {
                viewModelScope.launch {
                    val newState = state.value.copy(
                        showPopup = !state.value.showPopup
                    )
                    state.emit(newState)
                }
            }
            LoadTheme -> {
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
