package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    @Assisted private val backClicked: () -> Unit,
    private val datastoreRepository: DatastoreRepository,
    private val logoutInteractor: LogoutInteractor,
    private val logger: Logger,
    traktAuthRepository: TraktAuthRepository,
) : SettingsPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val logoutState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _state: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.DEFAULT_STATE)

    override val state: StateFlow<SettingsState> = combine(
        _state,
        datastoreRepository.observeImageQuality(),
        datastoreRepository.observeTheme(),
        traktAuthRepository.state,
        logoutState.observable,
    ) { currentState: SettingsState, imageQuality: ImageQuality, theme: AppTheme, authState: TraktAuthState, _: Boolean ->

        currentState.copy(
            imageQuality = imageQuality,
            appTheme = theme,
            isAuthenticated = authState == TraktAuthState.LOGGED_IN,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value,
    )

    override fun dispatch(action: SettingsActions) {
        when (action) {
            ChangeThemeClicked, DismissThemeClicked -> updateThemeDialogState()
            DismissTraktDialog, ShowTraktDialog -> updateTrackDialogState()
            ShowImageQualityDialog, DismissImageQualityDialog -> updateImageQualityDialogState()
            BackClicked -> backClicked()
            TraktLogoutClicked -> {
                coroutineScope.launch {
                    logoutInteractor(Unit)
                        .collectStatus(logoutState, logger, uiMessageManager)
                }
                updateTrackDialogState()
            }

            is ThemeSelected -> {
                datastoreRepository.saveTheme(action.appTheme)
                updateThemeDialogState()
            }

            is ImageQualitySelected -> {
                coroutineScope.launch {
                    datastoreRepository.saveImageQuality(action.quality)
                    updateImageQualityDialogState()
                }
            }
        }
    }

    private fun updateThemeDialogState() {
        coroutineScope.launch { _state.update { state -> state.copy(showthemePopup = !state.showthemePopup) } }
    }

    private fun updateTrackDialogState() {
        coroutineScope.launch { _state.update { state -> state.copy(showTraktDialog = !state.showTraktDialog) } }
    }

    private fun updateImageQualityDialogState() {
        coroutineScope.launch { _state.update { state -> state.copy(showImageQualityDialog = !state.showImageQualityDialog) } }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SettingsPresenter.Factory::class)
class DefaultSettingsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ) -> SettingsPresenter,
) : SettingsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ): SettingsPresenter = presenter(componentContext, backClicked)
}
