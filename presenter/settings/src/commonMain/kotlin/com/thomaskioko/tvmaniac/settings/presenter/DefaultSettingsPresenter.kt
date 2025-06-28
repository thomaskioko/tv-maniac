package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
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
        coroutineScope.launch {
            observeTheme()
            observeTraktAuthState()
        }
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
                launchWebView()
                coroutineScope.launch {
                    _state.update { state -> state.copy(showTraktDialog = !state.showTraktDialog) }
                }
            }

            TraktLogoutClicked -> {
                coroutineScope.launch { traktAuthRepository.clearAuth() }
            }
        }
    }

    private fun updateThemeDialogState(showDialog: Boolean) {
        coroutineScope.launch { _state.update { state -> state.copy(showthemePopup = showDialog) } }
    }

    private fun updateTrackDialogState(showDialog: Boolean) {
        coroutineScope.launch { _state.update { state -> state.copy(showTraktDialog = showDialog) } }
    }

    private suspend fun observeTheme() {
        datastoreRepository.observeTheme().collectLatest {
            _state.update { state -> state.copy(appTheme = it) }
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
