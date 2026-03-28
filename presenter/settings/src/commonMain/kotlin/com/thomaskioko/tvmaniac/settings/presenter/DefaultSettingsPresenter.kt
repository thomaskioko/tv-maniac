package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.api.ApplicationInfo
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
public class DefaultSettingsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val backClicked: () -> Unit,
    @Assisted private val onNavigateToDebugMenu: () -> Unit,
    private val appInfo: ApplicationInfo,
    private val datastoreRepository: DatastoreRepository,
    private val logoutInteractor: LogoutInteractor,
    private val toggleEpisodeNotificationsInteractor: ToggleEpisodeNotificationsInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    observeSettingsPreferencesInteractor: ObserveSettingsPreferencesInteractor,
    traktAuthRepository: TraktAuthRepository,
) : SettingsPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val logoutState = ObservableLoadingCounter()
    private val notificationToggleState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _state: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.DEFAULT_STATE)

    init {
        observeSettingsPreferencesInteractor(Unit)
    }

    override val state: StateFlow<SettingsState> = com.thomaskioko.tvmaniac.core.base.extensions.combine(
        _state,
        logoutState.observable,
        notificationToggleState.observable,
        observeSettingsPreferencesInteractor.flow,
        traktAuthRepository.state,
        uiMessageManager.message,
    ) { currentState, isLoggingOut, isTogglingNotifications, preferences, authState, message ->
        currentState.copy(
            isUpdating = isLoggingOut || isTogglingNotifications,
            imageQuality = preferences.imageQuality,
            theme = preferences.theme.toThemeModel(),
            openTrailersInYoutube = preferences.openTrailersInYoutube,
            includeSpecials = preferences.includeSpecials,
            isAuthenticated = authState == TraktAuthState.LOGGED_IN,
            backgroundSyncEnabled = preferences.backgroundSyncEnabled,
            lastSyncDate = preferences.lastSyncDate,
            showLastSyncDate = preferences.showLastSyncDate,
            versionName = appInfo.versionName,
            episodeNotificationsEnabled = preferences.episodeNotificationsEnabled,
            crashReportingEnabled = preferences.crashReportingEnabled,
            message = message,
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
            ShowAboutDialog, DismissAboutDialog -> updateAboutDialogState()
            VersionClicked -> handleVersionTap()
            BackClicked -> backClicked()
            TraktLogoutClicked -> {
                coroutineScope.launch {
                    logoutInteractor(Unit)
                        .collectStatus(logoutState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
                updateTrackDialogState()
            }

            is ThemeSelected -> {
                datastoreRepository.saveTheme(action.theme.toAppTheme())
                updateThemeDialogState()
            }

            is ImageQualitySelected -> {
                coroutineScope.launch {
                    datastoreRepository.saveImageQuality(action.quality)
                }
            }

            is YoutubeToggled -> {
                coroutineScope.launch {
                    datastoreRepository.saveOpenTrailersInYoutube(action.enabled)
                }
            }

            is IncludeSpecialsToggled -> {
                coroutineScope.launch {
                    datastoreRepository.saveIncludeSpecials(action.enabled)
                }
            }

            is BackgroundSyncToggled -> {
                coroutineScope.launch {
                    datastoreRepository.setBackgroundSyncEnabled(action.enabled)
                }
            }
            is EpisodeNotificationsToggled -> {
                coroutineScope.launch {
                    toggleEpisodeNotificationsInteractor(
                        ToggleEpisodeNotificationsInteractor.Params(enabled = action.enabled),
                    ).collectStatus(notificationToggleState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
            }

            is CrashReportingToggled -> {
                coroutineScope.launch {
                    datastoreRepository.setCrashReportingEnabled(action.enabled)
                }
            }

            is SettingsMessageShown -> {
                coroutineScope.launch {
                    uiMessageManager.clearMessage(action.id)
                }
            }
        }
    }

    private fun updateThemeDialogState() {
        _state.update { state -> state.copy(showthemePopup = !state.showthemePopup) }
    }

    private fun updateTrackDialogState() {
        _state.update { state -> state.copy(showTraktDialog = !state.showTraktDialog) }
    }

    private fun handleVersionTap() {
        _state.update { state ->
            val newCount = state.hiddenTapCount + 1
            if (newCount >= HIDDEN_TAP_THRESHOLD) {
                onNavigateToDebugMenu()
                state.copy(hiddenTapCount = 0)
            } else {
                state.copy(hiddenTapCount = newCount)
            }
        }
    }

    private fun updateAboutDialogState() {
        _state.update { state ->
            state.copy(
                showAboutDialog = !state.showAboutDialog,
                hiddenTapCount = 0,
            )
        }
    }

    private companion object {
        const val HIDDEN_TAP_THRESHOLD = 6
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SettingsPresenter.Factory::class)
public class DefaultSettingsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        backClicked: () -> Unit,
        onNavigateToDebugMenu: () -> Unit,
    ) -> SettingsPresenter,
) : SettingsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        backClicked: () -> Unit,
        onNavigateToDebugMenu: () -> Unit,
    ): SettingsPresenter = presenter(componentContext, backClicked, onNavigateToDebugMenu)
}
