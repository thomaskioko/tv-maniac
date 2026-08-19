package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.displayName
import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.accountswitcher.ConnectAndSwitchProviderInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.PrepareAccountSwitchInteractor
import com.thomaskioko.tvmaniac.domain.backup.ExportBackupInteractor
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.ObserveRewatchSupportInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObservePremiumAccessInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.AccountSwitchFlagQualifier
import com.thomaskioko.tvmaniac.featureflags.flags.SimklLoginFlagQualifier
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
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
import kotlin.time.Duration.Companion.minutes

@NavDestination(
    route = SettingsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class SettingsPresenter internal constructor(
    componentContext: ComponentContext,
    observeSettingsPreferencesInteractor: ObserveSettingsPreferencesInteractor,
    observePremiumAccessInteractor: ObservePremiumAccessInteractor,
    private val observeRewatchSupportInteractor: ObserveRewatchSupportInteractor,
    userRepository: UserRepository,
    private val navigator: Navigator,
    private val appMetadata: AppMetadata,
    private val datastoreRepository: DatastoreRepository,
    private val logoutInteractor: LogoutInteractor,
    private val toggleEpisodeNotificationsInteractor: ToggleEpisodeNotificationsInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val localizer: Localizer,
    private val logger: Logger,
    private val authManagers: Map<SyncProviderSource, AuthManager>,
    @SimklLoginFlagQualifier
    private val simklLoginFlag: FeatureFlag<Boolean>,
    @AccountSwitchFlagQualifier
    private val accountSwitchFlag: FeatureFlag<Boolean>,
    private val accountManager: AccountManager,
    private val prepareAccountSwitchInteractor: PrepareAccountSwitchInteractor,
    private val connectAndSwitchProviderInteractor: ConnectAndSwitchProviderInteractor,
    private val exportBackupInteractor: ExportBackupInteractor,
    private val labelsMapper: SettingsLabelsMapper,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val authProcessingState = ObservableLoadingCounter()
    private val notificationToggleState = ObservableLoadingCounter()
    private val backupExportState = ObservableLoadingCounter()
    private val accountSwitchState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _state: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.DEFAULT_STATE)

    private val backupLabels = BackupSettings(
        exportTitle = localizer.getString(StringResourceKey.SettingsBackupExportTitle),
        exportDescription = localizer.getString(StringResourceKey.SettingsBackupExportDescription),
    )

    init {
        observeSettingsPreferencesInteractor(Unit)
        observePremiumAccessInteractor(Unit)
        observeRewatchSyncNotice()
    }

    private fun observeRewatchSyncNotice() {
        observeRewatchSupportInteractor(Unit)
        coroutineScope.launch {
            observeRewatchSupportInteractor.flow.collect { supportsRewatch ->
                _state.update { state ->
                    state.copy(multiplePlaysSyncNotice = labelsMapper.rewatchSyncNotice(supportsRewatch))
                }
            }
        }
    }

    public val state: StateFlow<SettingsState> = combine(
        _state,
        authProcessingState.observable,
        notificationToggleState.observable,
        backupExportState.observable,
        accountSwitchState.observable,
        observeSettingsPreferencesInteractor.flow,
        accountManager.isConnected,
        accountManager.activeProvider,
        uiMessageManager.message,
        userRepository.observeCurrentUser().onStart { emit(null) },
        simklLoginFlag.observe(),
        accountSwitchFlag.observe(),
        observePremiumAccessInteractor.flow,
    ) { currentState, isProcessingAuth, isTogglingNotifications, isExportingBackup, isSwitchingAccount, preferences, isLoggedIn, activeProvider, message, userProfile, simklEnabled, accountSwitchEnabled, premiumAccess ->
        val username = userProfile?.let { it.fullName ?: it.username }
        val switchTarget = resolveSwitchTarget(isLoggedIn, activeProvider, simklEnabled, accountSwitchEnabled)
        currentState.copy(
            isLoading = false,
            isUpdating = isProcessingAuth || isTogglingNotifications,
            isProcessingAuth = isProcessingAuth,
            isSwitching = isSwitchingAccount,
            imageQuality = preferences.imageQuality,
            theme = preferences.theme.toThemeModel(),
            openTrailersInYoutube = preferences.openTrailersInYoutube,
            includeSpecials = preferences.includeSpecials,
            quickRateEnabled = preferences.quickRateEnabled,
            multiplePlaysEnabled = preferences.multiplePlaysEnabled,
            isAuthenticated = isLoggedIn,
            activeProvider = activeProvider,
            authProviders = authProviderOptions(simklEnabled),
            accountConnectedDescription = activeProvider?.let { connectedDescription(it) },
            switchTargetProvider = switchTarget,
            switchActionLabel = switchTarget?.let {
                localizer.getString(StringResourceKey.LabelAccountSwitchAction, it.displayName)
            },
            backgroundSyncEnabled = preferences.backgroundSyncEnabled,
            lastSyncDate = preferences.lastSyncDate,
            showLastSyncDate = preferences.showLastSyncDate,
            versionName = appMetadata.versionName,
            episodeNotificationsEnabled = preferences.episodeNotificationsEnabled,
            crashReportingEnabled = preferences.crashReportingEnabled,
            hapticFeedbackEnabled = preferences.layout.hapticFeedbackEnabled,
            newestSeasonFirst = preferences.layout.seasonSortOrder == SeasonSortOrder.NEWEST_FIRST,
            blurImage = preferences.layout.blurImage,
            discoverSectionToggles = buildDiscoverSectionToggles(preferences.layout.hiddenDiscoverSections),
            fontSizePercent = preferences.layout.fontSizePercent,
            posterWidth = preferences.layout.posterWidth,
            landscapeWidth = preferences.layout.landscapeWidth,
            posterCornerStyle = preferences.layout.posterCornerStyle,
            isDebugMenuEnabled = preferences.debugMenuEnabled,
            message = message,
            premium = labelsMapper.toPremiumState(premiumAccess),
            backup = currentState.backup.copy(
                exportTitle = backupLabels.exportTitle,
                exportDescription = backupLabels.exportDescription,
                isExporting = isExportingBackup,
            ),
            currentPageTitle = resolvePageTitle(currentState.currentPage),
            rootGroups = buildRootGroups(),
            username = username,
            labels = labelsMapper(
                imageQuality = preferences.imageQuality,
                showLastSyncDate = preferences.showLastSyncDate,
                lastSyncDate = preferences.lastSyncDate,
                versionName = appMetadata.versionName,
                username = username,
                isAuthenticated = isLoggedIn,
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
            DismissLogoutDialog, ShowLogoutDialog -> toggleLogoutConfirmation()
            VersionClicked -> handleVersionTap()
            BackClicked -> handleBackClicked()
            is OpenSettingsPage -> _state.update { state -> state.copy(currentPage = action.page) }
            AccountLogoutClicked -> {
                coroutineScope.launch {
                    logoutInteractor(Unit)
                        .collectStatus(authProcessingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
                toggleLogoutConfirmation()
            }

            is AccountLoginClicked -> {
                coroutineScope.launch {
                    authProcessingState.addLoader()
                    try {
                        authManagers[action.provider]?.launchWebView()
                    } finally {
                        authProcessingState.removeLoader()
                    }
                }
            }

            is SwitchProviderClicked -> handleSwitchClicked(action.provider)

            ConfirmSwitchDiscard -> handleConfirmSwitch()

            DismissSwitchDialog -> dismissSwitchDialog()

            is UpgradeToPremiumClicked -> Unit
            is ThemeSelected -> {
                if (!(action.theme.isPremium && state.value.premium.customThemesLocked)) {
                    datastoreRepository.saveTheme(action.theme.toTheme().toAppTheme())
                }
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

            is QuickRateToggled -> {
                if (state.value.premium.quickRateLocked) return
                coroutineScope.launch {
                    datastoreRepository.saveQuickRateEnabled(action.enabled)
                }
            }

            is MultiplePlaysToggled -> {
                coroutineScope.launch {
                    datastoreRepository.saveMultiplePlaysEnabled(action.enabled)
                }
            }

            is BackgroundSyncToggled -> {
                coroutineScope.launch {
                    datastoreRepository.setBackgroundSyncEnabled(action.enabled)
                }
            }
            is EpisodeNotificationsToggled -> {
                if (state.value.premium.episodeNotificationsLocked) return
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

            is HapticFeedbackToggled -> {
                coroutineScope.launch {
                    datastoreRepository.saveHapticFeedbackEnabled(action.enabled)
                }
            }

            is SeasonOrderToggled -> {
                coroutineScope.launch {
                    datastoreRepository.saveSeasonSortOrder(
                        if (action.enabled) SeasonSortOrder.NEWEST_FIRST else SeasonSortOrder.OLDEST_FIRST,
                    )
                }
            }

            is BlurUnwatchedToggled -> {
                coroutineScope.launch {
                    datastoreRepository.saveBlurUnwatchedEpisodeImages(action.enabled)
                }
            }

            is DiscoverSectionToggled -> {
                coroutineScope.launch {
                    datastoreRepository.updateDiscoverSectionVisibility(action.section, action.visible)
                }
            }

            is FontSizeChanged -> {
                coroutineScope.launch {
                    datastoreRepository.saveFontSizePercent(action.percent)
                }
            }

            is PosterWidthSelected -> {
                if (state.value.premium.posterStyleLocked) return
                coroutineScope.launch {
                    datastoreRepository.savePosterWidth(action.width)
                }
            }

            is LandscapeWidthSelected -> {
                if (state.value.premium.posterStyleLocked) return
                coroutineScope.launch {
                    datastoreRepository.saveLandscapeWidth(action.width)
                }
            }

            is PosterCornerStyleSelected -> {
                if (state.value.premium.posterStyleLocked) return
                coroutineScope.launch {
                    datastoreRepository.savePosterCornerStyle(action.style)
                }
            }

            is PosterStyleReset -> {
                if (state.value.premium.posterStyleLocked) return
                coroutineScope.launch {
                    datastoreRepository.savePosterWidth(PosterWidth.STANDARD)
                    datastoreRepository.saveLandscapeWidth(PosterWidth.STANDARD)
                    datastoreRepository.savePosterCornerStyle(PosterCornerStyle.SHARP)
                }
            }

            is BackupExportClicked -> handleBackupExportClicked()

            is BackupDestinationSelected -> handleBackupDestination(action.location)

            is BackupDestinationCancelled -> {
                _state.update { it.copy(backup = backupLabels) }
            }

            is SettingsMessageShown -> {
                coroutineScope.launch {
                    uiMessageManager.clearMessage(action.id)
                }
            }
        }
    }

    private fun handleBackupExportClicked() {
        if (state.value.premium.backupLocked) return
        _state.update { it.copy(backup = backupLabels.copy(awaitingDestination = true)) }
    }

    private fun handleBackupDestination(location: String) {
        if (state.value.premium.backupLocked) return
        _state.update { it.copy(backup = backupLabels) }
        coroutineScope.launch {
            exportBackupInteractor(ExportBackupInteractor.Params(location))
                .collectStatus(
                    counter = backupExportState,
                    logger = logger,
                    uiMessageManager = uiMessageManager,
                    sourceId = BACKUP_SOURCE_ID,
                    errorToStringMapper = errorToStringMapper,
                    successMessage = localizer.getString(StringResourceKey.SettingsBackupExportSuccess),
                )
        }
    }

    private fun handleBackClicked() {
        if (_state.value.currentPage != SettingsPage.ROOT) {
            _state.update { state -> state.copy(currentPage = parentOf(state.currentPage)) }
        } else {
            navigator.navigateBack()
        }
    }

    private fun parentOf(page: SettingsPage): SettingsPage = when (page) {
        SettingsPage.ROOT,
        SettingsPage.APPEARANCE,
        SettingsPage.BEHAVIOR,
        SettingsPage.NOTIFICATIONS,
        SettingsPage.PRIVACY,
        SettingsPage.INFO,
        SettingsPage.LICENSES,
        SettingsPage.ACCOUNT,
        SettingsPage.LAYOUT,
        SettingsPage.BACKUP,
        -> SettingsPage.ROOT

        SettingsPage.DISCOVER_SECTIONS,
        SettingsPage.POSTER_STYLE,
        -> SettingsPage.LAYOUT
    }

    private fun toggleLogoutConfirmation() {
        _state.update { state -> state.copy(showLogoutConfirmation = !state.showLogoutConfirmation) }
    }

    private fun resolveSwitchTarget(
        isLoggedIn: Boolean,
        activeProvider: SyncProviderSource?,
        simklEnabled: Boolean,
        accountSwitchEnabled: Boolean,
    ): SyncProviderSource? = when {
        !accountSwitchEnabled -> null
        !isLoggedIn -> null
        activeProvider == SyncProviderSource.TRAKT && simklEnabled -> SyncProviderSource.SIMKL
        activeProvider == SyncProviderSource.SIMKL -> SyncProviderSource.TRAKT
        else -> null
    }

    private fun handleSwitchClicked(target: SyncProviderSource) {
        coroutineScope.launch {
            accountSwitchState.addLoader()
            val count = prepareAccountSwitchInteractor()
            accountSwitchState.removeLoader()
            if (count > 0) {
                _state.update {
                    it.copy(
                        showSwitchConfirmation = true,
                        switchUnsavedCount = count,
                        pendingSwitchProvider = target,
                        switchDialogTitle = localizer.getString(
                            StringResourceKey.LabelAccountSwitchDialogTitle,
                            target.displayName,
                        ),
                        switchDialogMessage = localizer.getString(
                            StringResourceKey.LabelAccountSwitchDialogMessage,
                            count,
                        ),
                    )
                }
            } else {
                switchSyncProviderSource(target)
            }
        }
    }

    private fun handleConfirmSwitch() {
        val target = _state.value.pendingSwitchProvider ?: return
        _state.update {
            it.copy(
                showSwitchConfirmation = false,
                pendingSwitchProvider = null,
                switchUnsavedCount = 0,
                switchDialogTitle = null,
                switchDialogMessage = null,
            )
        }
        coroutineScope.launch { switchSyncProviderSource(target) }
    }

    private fun dismissSwitchDialog() {
        _state.update {
            it.copy(
                showSwitchConfirmation = false,
                pendingSwitchProvider = null,
                switchUnsavedCount = 0,
                switchDialogTitle = null,
                switchDialogMessage = null,
            )
        }
    }

    private suspend fun switchSyncProviderSource(target: SyncProviderSource) {
        connectAndSwitchProviderInteractor(ConnectAndSwitchProviderInteractor.Params(target))
            .collectStatus(
                counter = accountSwitchState,
                logger = logger,
                uiMessageManager = uiMessageManager,
                sourceId = ACCOUNT_SWITCH_SOURCE_ID,
                errorToStringMapper = errorToStringMapper,
            )
    }

    private fun handleVersionTap() {
        if (state.value.isDebugMenuEnabled) {
            navigator.navigateTo(DebugRoute)
            return
        }
        val newCount = _state.value.hiddenTapCount + 1
        if (newCount >= HIDDEN_TAP_THRESHOLD) {
            coroutineScope.launch { datastoreRepository.setDebugMenuEnabled(true) }
            navigator.navigateTo(DebugRoute)
            _state.update { it.copy(hiddenTapCount = 0) }
        } else {
            _state.update { it.copy(hiddenTapCount = newCount) }
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
            SettingsPage.ACCOUNT -> StringResourceKey.SettingsTitleAccount
            SettingsPage.LAYOUT -> StringResourceKey.SettingsLayoutTitle
            SettingsPage.DISCOVER_SECTIONS -> StringResourceKey.SettingsDiscoverSectionsTitle
            SettingsPage.POSTER_STYLE -> StringResourceKey.SettingsPosterStyleTitle
            SettingsPage.BACKUP -> StringResourceKey.SettingsBackupTitle
        },
    )

    private fun buildDiscoverSectionToggles(hidden: Set<DiscoverSection>): ImmutableList<DiscoverSectionToggle> =
        DiscoverSection.entries.map { section ->
            DiscoverSectionToggle(
                section = section,
                label = localizer.getString(discoverSectionLabelKey(section)),
                visible = section !in hidden,
            )
        }.toImmutableList()

    private fun discoverSectionLabelKey(section: DiscoverSection): StringResourceKey = when (section) {
        DiscoverSection.START_WATCHING -> StringResourceKey.LabelStartWatching
        DiscoverSection.TRENDING_TODAY -> StringResourceKey.LabelDiscoverTrendingToday
        DiscoverSection.UPCOMING -> StringResourceKey.LabelDiscoverUpcoming
        DiscoverSection.POPULAR -> StringResourceKey.LabelDiscoverPopular
        DiscoverSection.TOP_RATED -> StringResourceKey.LabelDiscoverTopRated
    }

    private fun authProviderOptions(simklEnabled: Boolean): ImmutableList<AuthProviderOption> =
        buildList {
            add(providerOption(SyncProviderSource.TRAKT))
            if (simklEnabled) add(providerOption(SyncProviderSource.SIMKL))
        }.toImmutableList()

    private fun providerOption(provider: SyncProviderSource): AuthProviderOption = AuthProviderOption(
        provider = provider,
        label = localizer.getString(StringResourceKey.LabelAuthContinueWith, provider.displayName),
    )

    private fun connectedDescription(provider: SyncProviderSource): String = localizer.getString(
        when (provider) {
            SyncProviderSource.TRAKT -> StringResourceKey.LabelSettingsTraktConnectedDescription
            SyncProviderSource.SIMKL -> StringResourceKey.LabelSettingsSimklConnectedDescription
        },
    )

    private fun buildRootGroups(): ImmutableList<SettingsCategoryGroup> =
        buildList {
            add(
                SettingsCategoryGroup(
                    label = localizer.getString(StringResourceKey.LabelSettingsGroupAccount),
                    items = persistentListOf(
                        SettingsCategoryItem(
                            page = SettingsPage.ACCOUNT,
                            title = localizer.getString(StringResourceKey.SettingsTitleAccount),
                            summary = localizer.getString(StringResourceKey.LabelSettingsAccountDescription),
                        ),
                    ),
                ),
            )
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
                            page = SettingsPage.LAYOUT,
                            title = localizer.getString(StringResourceKey.SettingsLayoutTitle),
                            summary = localizer.getString(StringResourceKey.SettingsLayoutDescription),
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
                        SettingsCategoryItem(
                            page = SettingsPage.BACKUP,
                            title = localizer.getString(StringResourceKey.SettingsBackupTitle),
                            summary = localizer.getString(StringResourceKey.SettingsBackupDescription),
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

    private companion object {
        private const val HIDDEN_TAP_THRESHOLD = 6
        private const val TAG = "SettingsPresenter"
        private const val BACKUP_SOURCE_ID = "BackupExport"
        private const val ACCOUNT_SWITCH_SOURCE_ID = "AccountSwitch"
        private val OAUTH_TIMEOUT = 2.minutes
    }
}
