package com.thomaskioko.tvmaniac.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.settings.api.SettingsActions
import com.thomaskioko.tvmaniac.settings.api.SettingsContent
import com.thomaskioko.tvmaniac.settings.api.SettingsEffect
import com.thomaskioko.tvmaniac.settings.api.SettingsRepository
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.ObserveTraktUserInteractor
import com.thomaskioko.tvmaniac.traktauth.ObserveTraktAuthStateInteractor
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.TraktManager
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
    private val settingsRepository: SettingsRepository,
    private val traktManager: TraktManager,
    private val traktAuthManager: TraktAuthManager,
    private val traktAuthInteractor: ObserveTraktAuthStateInteractor,
    private val observeTraktUserInteractor: ObserveTraktUserInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : Store<SettingsContent, SettingsActions, SettingsEffect>, ViewModel(),
    TraktAuthManager by traktAuthManager {

    override val state = MutableStateFlow(SettingsContent.DEFAULT)

    private val sideEffect = MutableSharedFlow<SettingsEffect>()

    init {
        dispatch(SettingsActions.LoadTheme)

        viewModelScope.launch {
            traktAuthInteractor.invoke(Unit)
                .collect {

                    val newState = state.value.copy(
                        loggedIn = it == TraktAuthState.LOGGED_IN
                    )
                    state.emit(newState)

                    when (it) {
                        TraktAuthState.LOGGED_IN -> dispatch(SettingsActions.FetchTraktUserProfile)
                        TraktAuthState.LOGGED_OUT -> dispatch(SettingsActions.RefreshTraktAuthToken)
                    }
                }
        }
    }

    override fun observeState(): StateFlow<SettingsContent> = state

    override fun observeSideEffect(): Flow<SettingsEffect> = sideEffect

    override fun dispatch(action: SettingsActions) {
        when (action) {
            is SettingsActions.ThemeSelected -> {
                viewModelScope.launch(context = ioDispatcher) {
                    settingsRepository.saveTheme(action.theme)
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

            SettingsActions.DismissTraktDialog -> {
                viewModelScope.launch(context = ioDispatcher) {
                    val newState = state.value.copy(
                        showTraktDialog = false
                    )
                    state.emit(newState)
                }
            }
            SettingsActions.ShowTraktDialog -> {
                viewModelScope.launch(context = ioDispatcher) {
                    val newState = state.value.copy(
                        showTraktDialog = true
                    )
                    state.emit(newState)
                }
            }

            SettingsActions.TraktLogin -> {
                viewModelScope.launch {
                    val newState = state.value.copy(
                        showTraktDialog = false
                    )
                    state.emit(newState)
                    traktManager.clearAuth()
                }
            }

            SettingsActions.LoadTheme -> updateTheme()
            SettingsActions.TraktLogout -> logoutOfTrakt()
            SettingsActions.FetchTraktUserProfile -> fetchUserInfo()
            SettingsActions.RefreshTraktAuthToken -> {

            }
        }
    }

    private fun updateTheme() {
        viewModelScope.launch {
            settingsRepository.observeTheme()
                .collect {
                    val newState = state.value.copy(
                        theme = it,
                    )
                    state.emit(newState)
                }
        }
    }

    private fun logoutOfTrakt() {
        viewModelScope.launch {
            val newState = state.value.copy(
                showTraktDialog = false
            )
            state.emit(newState)
            traktManager.clearAuth()
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            observeTraktUserInteractor.invoke("me")
                .collect {
                    val newState = state.value.copy(
                        traktUserName = it.userName,
                        traktFullName = it.fullName,
                        traktUserPicUrl = it.profilePicUrl
                    )
                    state.emit(newState)
                }
        }
    }
}
