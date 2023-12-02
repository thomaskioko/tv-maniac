package com.thomaskioko.tvmaniac.presentation.profile

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias ProfilePresenterFactory = (
    ComponentContext,
    goBack: () -> Unit,
    navigateToSettings: () -> Unit,
    launchTraktWebView: () -> Unit,
) -> ProfilePresenter

@Inject
class ProfilePresenter(
    dispatchersProvider: AppCoroutineDispatchers,
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToSettings: () -> Unit,
    @Assisted private val launchTraktWebView: () -> Unit,
    private val traktAuthRepository: TraktAuthRepository,
    private val datastoreRepository: DatastoreRepository,
    private val profileRepository: ProfileRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatchersProvider.main)

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

            TraktLoginClicked -> {
                launchTraktWebView()
                coroutineScope.launch {
                    _state.update { state ->
                        state.copy(showTraktDialog = !state.showTraktDialog)
                    }
                }
            }

            TraktLogoutClicked -> {
                coroutineScope.launch {
                    traktAuthRepository.clearAuth()
                    profileRepository.clearProfile()

                    _state.update {
                        ProfileState()
                    }
                }
            }

            SettingsClicked -> navigateToSettings()
        }
    }

    private fun updateDialogState(showDialog: Boolean) {
        coroutineScope.launch {
            _state.update { state ->
                state.copy(showTraktDialog = showDialog)
            }
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
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
        coroutineScope.launch {
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
