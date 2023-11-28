package com.thomaskioko.tvmaniac.presentation.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class ProfileScreenModel @Inject constructor(
    private val traktAuthRepository: TraktAuthRepository,
    private val datastoreRepository: DatastoreRepository,
    private val profileRepository: ProfileRepository,
) : ScreenModel {

    private val _state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeAuthState()
        observeProfile()
    }

    fun dispatch(action: ProfileActions) {
        when (action) {
            DismissTraktDialog -> updateDialogState(false)
            ShowTraktDialog -> updateDialogState(true)

            TraktLoginClicked ->
                screenModelScope.launch {
                    _state.update { state ->
                        state.copy(showTraktDialog = !state.showTraktDialog)
                    }
                }

            TraktLogoutClicked -> {
                screenModelScope.launch {
                    traktAuthRepository.clearAuth()
                    profileRepository.clearProfile()

                    _state.update {
                        ProfileState()
                    }
                }
            }
        }
    }

    private fun updateDialogState(showDialog: Boolean) {
        screenModelScope.launch {
            _state.update { state ->
                state.copy(showTraktDialog = showDialog)
            }
        }
    }

    private fun observeAuthState() {
        screenModelScope.launch {
            datastoreRepository.observeAuthState()
                .collectLatest { authState ->
                    _state.update { state ->
                        if (authState.isAuthorized) {
                            ProfileState()
                        } else {
                            state
                        }
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
                                (state as? ProfileState)?.copy(
                                    isLoading = false,
                                    errorMessage = failure.errorMessage,
                                ) ?: state
                            }
                        },
                        { useInfo ->
                            _state.update { state ->
                                (state as? ProfileState)?.copy(
                                    isLoading = false,
                                    userInfo = useInfo?.let {
                                        UserInfo(
                                            slug = it.slug,
                                            userName = it.user_name,
                                            fullName = it.full_name,
                                            userPicUrl = it.profile_picture,
                                        )
                                    },
                                ) ?: state
                            }
                        },
                    )
                }
        }
    }
}
