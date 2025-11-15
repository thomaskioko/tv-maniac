package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, SettingsPresenter::class)
class DefaultSettingsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val launchWebView: () -> Unit,
    private val datastoreRepository: DatastoreRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : SettingsPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState.DEFAULT_STATE)

    init {
        initializeObservers()
    }

    override val state: StateFlow<SettingsState> = _state.asStateFlow()

    override fun dispatch(action: SettingsActions) {
        when (action) {
            ChangeThemeClicked -> updateThemeDialogState(true)
            DismissThemeClicked -> updateThemeDialogState(false)
            DismissTraktDialog -> updateTrackDialogState(false)
            ShowTraktDialog -> updateTrackDialogState(true)
            is ThemeSelected -> {
                datastoreRepository.saveTheme(action.appTheme)
                updateThemeDialogState(false)
            }

            TraktLoginClicked -> {
                coroutineScope.launch {
                    _state.update { state -> state.copy(showTraktDialog = false) }

                    val currentState = traktAuthRepository.getAuthState()

                    if (currentState?.isAuthorized == true) {
                        val refreshed = traktAuthRepository.refreshTokens()
                        if (refreshed != null) {
                            return@launch
                        }
                    }

                    launchWebView()
                }
            }

            TraktLogoutClicked -> {
                coroutineScope.launch { traktAuthRepository.logout() }
            }

            ShowImageQualityDialog -> {
                updateImageQualityDialogState(true)
            }

            DismissImageQualityDialog -> {
                updateImageQualityDialogState(false)
            }

            is ImageQualitySelected -> {
                coroutineScope.launch {
                    datastoreRepository.saveImageQuality(action.quality)
                    updateImageQualityDialogState(false)
                }
            }
        }
    }

    private fun initializeObservers() {
        coroutineScope.launch {
            observeTheme()
        }
        coroutineScope.launch {
            observeImageQuality()
        }
        coroutineScope.launch {
            observeAuthenticating()
        }
        coroutineScope.launch {
            observeAuthError()
        }
        coroutineScope.launch {
            observeTraktAuthState()
        }
    }

    private fun updateThemeDialogState(showDialog: Boolean) {
        coroutineScope.launch { _state.update { state -> state.copy(showthemePopup = showDialog) } }
    }

    private fun updateTrackDialogState(showDialog: Boolean) {
        coroutineScope.launch { _state.update { state -> state.copy(showTraktDialog = showDialog) } }
    }

    private fun updateImageQualityDialogState(showDialog: Boolean) {
        coroutineScope.launch { _state.update { state -> state.copy(showImageQualityDialog = showDialog) } }
    }

    private suspend fun observeTheme() {
        datastoreRepository.observeTheme().collectLatest {
            _state.update { state -> state.copy(appTheme = it) }
        }
    }

    private suspend fun observeImageQuality() {
        datastoreRepository.observeImageQuality().collectLatest { quality ->
            _state.update { state -> state.copy(imageQuality = quality) }
        }
    }

    private suspend fun observeAuthenticating() {
        traktAuthRepository.isAuthenticating.collectLatest { isAuthenticating ->
            _state.update { state -> state.copy(isLoading = isAuthenticating) }
        }
    }

    private suspend fun observeAuthError() {
        traktAuthRepository.authError.collectLatest { error ->
            _state.update { state ->
                state.copy(
                    errorMessage = error?.let { mapAuthErrorToMessage(it) },
                    showTraktDialog = if (error != null) false else state.showTraktDialog,
                )
            }
        }
    }

    private suspend fun observeTraktAuthState() {
        traktAuthRepository.state.collectLatest { authState ->
            _state.update { state ->
                state.copy(
                    isAuthenticated = authState == TraktAuthState.LOGGED_IN,
                )
            }
        }
    }

    private fun mapAuthErrorToMessage(error: AuthError): String {
        // TODO:: Get Strings from localizer
        return when (error) {
            is AuthError.NetworkError -> "No internet connection. Please check your network."
            is AuthError.OAuthCancelled -> "Authentication cancelled."
            is AuthError.OAuthFailed -> "Authentication failed: ${error.message}"
            is AuthError.TokenExchangeFailed -> "Failed to complete authentication."
            is AuthError.Unknown -> "An error occurred. Please try again."
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SettingsPresenter.Factory::class)
class DefaultSettingsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        launchWebView: () -> Unit,
    ) -> SettingsPresenter,
) : SettingsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        launchWebView: () -> Unit,
    ): SettingsPresenter = presenter(componentContext, launchWebView)
}
