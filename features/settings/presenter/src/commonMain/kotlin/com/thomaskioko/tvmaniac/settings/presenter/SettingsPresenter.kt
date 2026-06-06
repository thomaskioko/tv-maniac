package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@NavDestination(
    route = SettingsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class SettingsPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val appMetadata: AppMetadata,
    private val datastoreRepository: DatastoreRepository,
    private val userRepository: UserRepository,
    private val logoutInteractor: LogoutInteractor,
    private val toggleEpisodeNotificationsInteractor: ToggleEpisodeNotificationsInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val localizer: Localizer,
    private val logger: Logger,
    private val traktAuthManager: TraktAuthManager,
    observeSettingsPreferencesInteractor: ObserveSettingsPreferencesInteractor,
    connectedAccountRepository: ConnectedAccountRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val logoutState = ObservableLoadingCounter()
    private val notificationToggleState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _state: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.DEFAULT_STATE)

    init {
        observeSettingsPreferencesInteractor(Unit)
    }

    public val state: StateFlow<SettingsState> = com.thomaskioko.tvmaniac.core.base.extensions.combine(
        _state,
        logoutState.observable,
        notificationToggleState.observable,
        observeSettingsPreferencesInteractor.flow,
        connectedAccountRepository.isConnected,
        uiMessageManager.message,
        userRepository.observeCurrentUser().onStart { emit(null) },
    ) { currentState, isLoggingOut, isTogglingNotifications, preferences, isLoggedIn, message, userProfile ->
        val isAuthenticated = isLoggedIn
        val username = userProfile?.let { it.fullName ?: it.username }
        currentState.copy(
            isLoading = false,
            isUpdating = isLoggingOut || isTogglingNotifications,
            imageQuality = preferences.imageQuality,
            theme = preferences.theme.toThemeModel(),
            openTrailersInYoutube = preferences.openTrailersInYoutube,
            includeSpecials = preferences.includeSpecials,
            isAuthenticated = isAuthenticated,
            backgroundSyncEnabled = preferences.backgroundSyncEnabled,
            lastSyncDate = preferences.lastSyncDate,
            showLastSyncDate = preferences.showLastSyncDate,
            versionName = appMetadata.versionName,
            episodeNotificationsEnabled = preferences.episodeNotificationsEnabled,
            crashReportingEnabled = preferences.crashReportingEnabled,
            message = message,
            currentPageTitle = resolvePageTitle(currentState.currentPage),
            rootGroups = buildRootGroups(isAuthenticated),
            username = username,
            labels = buildLabels(
                imageQuality = preferences.imageQuality,
                showLastSyncDate = preferences.showLastSyncDate,
                lastSyncDate = preferences.lastSyncDate,
                versionName = appMetadata.versionName,
                username = username,
                isAuthenticated = isAuthenticated,
            ),
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value,
    )

    public val stateValue: Value<SettingsState> = state.asValue(coroutineScope)

    public fun dispatch(action: SettingsActions) {
        when (action) {
            DismissTraktDialog, ShowTraktDialog -> updateTrackDialogState()
            VersionClicked -> handleVersionTap()
            BackClicked -> handleBackClicked()
            is OpenSettingsPage -> _state.update { state -> state.copy(currentPage = action.page) }
            TraktLogoutClicked -> {
                coroutineScope.launch {
                    logoutInteractor(Unit)
                        .collectStatus(logoutState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
                updateTrackDialogState()
            }

            TraktLoginClicked -> {
                coroutineScope.launch {
                    traktAuthManager.launchWebView()
                }
            }

            is ThemeSelected -> {
                datastoreRepository.saveTheme(action.theme.toTheme().toAppTheme())
            }

            is ImageQualitySelected -> {
                coroutineScope.launch {
                    datastoreRepository.saveImageQuality(
                        com.thomaskioko.tvmaniac.datastore.api.ImageQuality.valueOf(action.quality.name),
                    )
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

    private fun handleBackClicked() {
        if (_state.value.currentPage != SettingsPage.ROOT) {
            _state.update { state -> state.copy(currentPage = SettingsPage.ROOT) }
        } else {
            navigator.navigateBack()
        }
    }

    private fun updateTrackDialogState() {
        _state.update { state -> state.copy(showTraktDialog = !state.showTraktDialog) }
    }

    private fun handleVersionTap() {
        _state.update { state ->
            val newCount = state.hiddenTapCount + 1
            if (newCount >= HIDDEN_TAP_THRESHOLD) {
                navigator.navigateTo(DebugRoute)
                state.copy(hiddenTapCount = 0)
            } else {
                state.copy(hiddenTapCount = newCount)
            }
        }
    }

    private fun resolvePageTitle(page: SettingsPage): String = localizer.getString(
        when (page) {
            SettingsPage.ROOT -> StringResourceKey.TitleSettings
            SettingsPage.APPEARANCE -> StringResourceKey.LabelSettingsSectionAppearance
            SettingsPage.BEHAVIOR -> StringResourceKey.LabelSettingsSectionBehavior
            SettingsPage.NOTIFICATIONS -> StringResourceKey.LabelSettingsSectionNotifications
            SettingsPage.PRIVACY -> StringResourceKey.LabelSettingsSectionPrivacy
            SettingsPage.INFO -> StringResourceKey.SettingsTitleInfo
            SettingsPage.LICENSES -> StringResourceKey.LabelSettingsSectionLicenses
            SettingsPage.TRAKT -> StringResourceKey.SettingsTitleTrakt
        },
    )

    private fun buildRootGroups(isAuthenticated: Boolean): ImmutableList<SettingsCategoryGroup> =
        buildList {
            if (isAuthenticated) {
                add(
                    SettingsCategoryGroup(
                        label = localizer.getString(StringResourceKey.LabelSettingsGroupAccount),
                        items = persistentListOf(
                            SettingsCategoryItem(
                                page = SettingsPage.TRAKT,
                                title = localizer.getString(StringResourceKey.SettingsTitleTrakt),
                                summary = localizer.getString(StringResourceKey.LabelSettingsTraktDescription),
                            ),
                        ),
                    ),
                )
            }
            add(
                SettingsCategoryGroup(
                    label = localizer.getString(StringResourceKey.LabelSettingsGroupGeneral),
                    items = persistentListOf(
                        SettingsCategoryItem(
                            page = SettingsPage.APPEARANCE,
                            title = localizer.getString(StringResourceKey.LabelSettingsSectionAppearance),
                            summary = localizer.getString(StringResourceKey.LabelSettingsAppearanceDescription),
                        ),
                        SettingsCategoryItem(
                            page = SettingsPage.BEHAVIOR,
                            title = localizer.getString(StringResourceKey.LabelSettingsSectionBehavior),
                            summary = localizer.getString(StringResourceKey.LabelSettingsBehaviorDescription),
                        ),
                        SettingsCategoryItem(
                            page = SettingsPage.NOTIFICATIONS,
                            title = localizer.getString(StringResourceKey.LabelSettingsSectionNotifications),
                            summary = localizer.getString(StringResourceKey.LabelSettingsNotificationsDescription),
                        ),
                        SettingsCategoryItem(
                            page = SettingsPage.PRIVACY,
                            title = localizer.getString(StringResourceKey.LabelSettingsSectionPrivacy),
                            summary = localizer.getString(StringResourceKey.LabelSettingsPrivacyDescription),
                        ),
                    ),
                ),
            )
            add(
                SettingsCategoryGroup(
                    label = localizer.getString(StringResourceKey.SettingsAboutSectionTitle),
                    items = persistentListOf(
                        SettingsCategoryItem(
                            page = SettingsPage.INFO,
                            title = localizer.getString(StringResourceKey.SettingsTitleInfo),
                            summary = localizer.getString(StringResourceKey.LabelSettingsInfoDescription),
                        ),
                        SettingsCategoryItem(
                            page = SettingsPage.LICENSES,
                            title = localizer.getString(StringResourceKey.LabelSettingsSectionLicenses),
                            summary = localizer.getString(StringResourceKey.LabelSettingsLicensesDescription),
                        ),
                    ),
                ),
            )
        }.toImmutableList()

    private fun imageQualityDescriptionKey(quality: ImageQuality): StringResourceKey = when (quality) {
        ImageQuality.AUTO -> StringResourceKey.LabelSettingsImageQualityAutoDescription
        ImageQuality.HIGH -> StringResourceKey.LabelSettingsImageQualityHighDescription
        ImageQuality.MEDIUM -> StringResourceKey.LabelSettingsImageQualityMediumDescription
        ImageQuality.LOW -> StringResourceKey.LabelSettingsImageQualityLowDescription
    }

    private fun buildLabels(
        imageQuality: ImageQuality,
        showLastSyncDate: Boolean,
        lastSyncDate: String?,
        versionName: String,
        username: String?,
        isAuthenticated: Boolean,
    ): SettingsLabels = SettingsLabels(
        back = localizer.getString(StringResourceKey.CdBack),
        themeTitle = localizer.getString(StringResourceKey.SettingsThemeSelectorTitle),
        themeSubtitle = localizer.getString(StringResourceKey.SettingsThemeSelectorSubtitle),
        imageQualityTitle = localizer.getString(StringResourceKey.LabelSettingsImageQuality),
        imageQualityDescription = localizer.getString(imageQualityDescriptionKey(imageQuality)),
        imageQualityAuto = localizer.getString(StringResourceKey.LabelSettingsImageQualityAuto),
        imageQualityHigh = localizer.getString(StringResourceKey.LabelSettingsImageQualityHigh),
        imageQualityMedium = localizer.getString(StringResourceKey.LabelSettingsImageQualityMedium),
        imageQualityLow = localizer.getString(StringResourceKey.LabelSettingsImageQualityLow),
        syncTitle = localizer.getString(StringResourceKey.LabelSettingsSyncUpdate),
        syncDescription = localizer.getString(StringResourceKey.LabelSettingsSyncUpdateDescription),
        lastSync = if (showLastSyncDate && lastSyncDate != null) {
            localizer.getString(StringResourceKey.LabelSettingsLastSyncDate, lastSyncDate)
        } else {
            null
        },
        includeSpecialsTitle = localizer.getString(StringResourceKey.LabelSettingsIncludeSpecials),
        includeSpecialsDescription = localizer.getString(StringResourceKey.LabelSettingsIncludeSpecialsDescription),
        youtubeTitle = localizer.getString(StringResourceKey.LabelSettingsYoutube),
        youtubeDescription = localizer.getString(StringResourceKey.LabelSettingsYoutubeDescription),
        episodeNotificationsTitle = localizer.getString(StringResourceKey.LabelSettingsEpisodeNotifications),
        episodeNotificationsDescription = localizer.getString(StringResourceKey.LabelSettingsEpisodeNotificationsDescription),
        crashReportingTitle = localizer.getString(StringResourceKey.LabelSettingsCrashReporting),
        crashReportingDescription = localizer.getString(StringResourceKey.LabelSettingsCrashReportingDescription),
        privacyPolicy = localizer.getString(StringResourceKey.LabelSettingsPrivacyPolicy),
        appName = localizer.getString(StringResourceKey.SettingsAboutAppName),
        version = localizer.getString(StringResourceKey.SettingsAboutVersion, versionName),
        aboutDescription = localizer.getString(StringResourceKey.SettingsAboutDescription),
        sourceCode = localizer.getString(StringResourceKey.SettingsAboutSourceCode),
        github = localizer.getString(StringResourceKey.SettingsAboutGithub),
        apiDisclaimer = localizer.getString(StringResourceKey.SettingsAboutApiDisclaimer),
        licensesApp = localizer.getString(StringResourceKey.LabelSettingsLicensesSectionApp),
        licensesData = localizer.getString(StringResourceKey.LabelSettingsLicensesSectionData),
        tmdbTitle = localizer.getString(StringResourceKey.LabelSettingsLicensesTmdbTitle),
        tmdbBody = localizer.getString(StringResourceKey.LabelSettingsLicensesTmdbBody),
        traktBody = localizer.getString(StringResourceKey.LabelSettingsLicensesTraktBody),
        traktTitle = localizer.getString(StringResourceKey.SettingsTitleTraktApp),
        traktDescription = localizer.getString(StringResourceKey.TraktDescription),
        traktAuthentication = localizer.getString(StringResourceKey.LabelSettingsTraktAuthentication),
        traktConnected = when {
            !isAuthenticated -> localizer.getString(StringResourceKey.LabelSettingsTraktConnect)
            username != null -> localizer.getString(StringResourceKey.LabelSettingsTraktConnectedAs, username)
            else -> localizer.getString(StringResourceKey.LabelSettingsTraktConnected)
        },
        traktConnectedDescription = if (isAuthenticated) {
            localizer.getString(StringResourceKey.LabelSettingsTraktConnectedDescription)
        } else {
            localizer.getString(StringResourceKey.SettingsTraktDetailDescription)
        },
        logout = localizer.getString(StringResourceKey.Logout),
        login = localizer.getString(StringResourceKey.Login),
    )

    private companion object {
        const val HIDDEN_TAP_THRESHOLD = 6
    }
}
