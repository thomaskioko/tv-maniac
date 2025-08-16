package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
class SettingsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted("toWebView") private val launchWebView: () -> Unit,
    private val datastoreRepository: DatastoreRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState.DEFAULT_STATE)

    init {
        initializeObservers()
    }

    public val state: StateFlow<SettingsState> = _state.asStateFlow()

    public fun dispatch(action: SettingsActions) {
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
                launchWebView()
                coroutineScope.launch {
                    _state.update { state -> state.copy(showTraktDialog = !state.showTraktDialog) }
                }
            }

            TraktLogoutClicked -> {
                coroutineScope.launch { traktAuthRepository.clearAuth() }
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
            observeTraktAuthState()
        }
        coroutineScope.launch {
            observeImageQuality()
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

    private suspend fun observeTraktAuthState() {
        traktAuthRepository.observeState().collectLatest { result ->
            when (result) {
                TraktAuthState.LOGGED_IN -> {}
                TraktAuthState.LOGGED_OUT -> {
                    datastoreRepository.clearAuthState()
                    traktAuthRepository.clearAuth()
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(
            @Assisted componentContext: ComponentContext,
            @Assisted("toWebView") launchWebView: () -> Unit,
        ): SettingsPresenter
    }
}
