package com.thomaskioko.tvmaniac.presentation.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class SettingsScreenModel @Inject constructor(
    private val datastoreRepository: DatastoreRepository,
    private val profileRepository: ProfileRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : ScreenModel {

    private val _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState.DEFAULT_STATE)
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            observeTheme()
            observeTraktAuthState()
            observeProfile()
        }
    }

    fun dispatch(action: SettingsActions) {
        when (action) {
            ChangeThemeClicked -> updateThemeDialogState(true)
            DismissThemeClicked -> updateThemeDialogState(false)
            DismissTraktDialog -> updateTrackDialogState(false)
            ShowTraktDialog -> updateTrackDialogState(true)
            is ThemeSelected -> {
                datastoreRepository.saveTheme(action.theme)
                updateThemeDialogState(false)
            }

            TraktLoginClicked -> {
                screenModelScope.launch {
                    _state.update { state ->
                        state.copy(showTraktDialog = !state.showTraktDialog)
                    }
                }
            }

            TraktLogoutClicked -> {
                screenModelScope.launch {
                    traktAuthRepository.clearAuth()
                    profileRepository.clearProfile()

                    _state.update {
                        it.copy(userInfo = null)
                    }
                }
            }
        }
    }

    private fun updateThemeDialogState(showDialog: Boolean) {
        screenModelScope.launch {
            _state.update { state ->
                state.copy(showthemePopup = showDialog)
            }
        }
    }

    private fun updateTrackDialogState(showDialog: Boolean) {
        screenModelScope.launch {
            _state.update { state ->
                state.copy(showTraktDialog = showDialog)
            }
        }
    }
    private suspend fun observeTheme() {
        datastoreRepository.observeTheme()
            .collectLatest {
                _state.update { state ->
                    state.copy(theme = it)
                }
            }
    }

    private suspend fun observeTraktAuthState() {
        traktAuthRepository.observeState()
            .collectLatest { result ->
                when (result) {
                    TraktAuthState.LOGGED_IN -> {}
                    TraktAuthState.LOGGED_OUT -> {
                        datastoreRepository.clearAuthState()
                        traktAuthRepository.clearAuth()

                        _state.update { it.copy(userInfo = null) }
                    }
                }
            }
    }

    private fun observeProfile() {
        screenModelScope.launch {
            profileRepository.observeProfile("me")
                .collectLatest { response ->
                    response.fold(
                        { failure ->
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    errorMessage = failure.errorMessage,
                                )
                            }
                        },
                        { useInfo ->
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    userInfo = useInfo?.let {
                                        UserInfo(
                                            slug = it.slug,
                                            userName = it.user_name,
                                            fullName = it.full_name,
                                            userPicUrl = it.profile_picture,
                                        )
                                    },
                                )
                            }
                        },
                    )
                }
        }
    }
}
