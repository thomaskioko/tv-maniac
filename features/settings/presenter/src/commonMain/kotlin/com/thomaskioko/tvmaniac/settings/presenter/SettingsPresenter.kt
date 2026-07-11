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
import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.accountswitcher.CountUnsavedChanges
import com.thomaskioko.tvmaniac.domain.accountswitcher.PushPendingChangesInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.SwitchAccountInteractor
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.AccountSwitchFlagQualifier
import com.thomaskioko.tvmaniac.featureflags.flags.SimklLoginFlagQualifier
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.minutes

@NavDestination(
    route = SettingsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class SettingsPresenter(
    componentContext: ComponentContext,
    observeSettingsPreferencesInteractor: ObserveSettingsPreferencesInteractor,
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
    private val subscriptionManager: SubscriptionManager,
    private val pushPendingChangesInteractor: PushPendingChangesInteractor,
    private val countUnsavedChanges: CountUnsavedChanges,
    private val switchAccountInteractor: SwitchAccountInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val authProcessingState = ObservableLoadingCounter()
    private val notificationToggleState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val _state: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.DEFAULT_STATE)

    private val locksFlow = kotlinx.coroutines.flow.combine(
        subscriptionManager.observeAccess(SubscriptionFeature.CustomThemes),
        subscriptionManager.observeAccess(SubscriptionFeature.EpisodeNotifications),
    ) { customThemesAccess, episodeNotificationsAccess ->
        SettingsLocks(
            customThemesLocked = !customThemesAccess,
            episodeNotificationsLocked = !episodeNotificationsAccess,
            badgeText = localizer.getString(StringResourceKey.LabelPremiumBadge),
            themesLockedTitle = localizer.getString(StringResourceKey.LabelThemesLockedTitle),
            themesLockedMessage = localizer.getString(StringResourceKey.LabelThemesLockedMessage),
            upgradeText = localizer.getString(StringResourceKey.LabelUpgradeToPremium),
            lockedContentDescription = localizer.getString(StringResourceKey.CdLocked),
        )
    }

    init {
        observeSettingsPreferencesInteractor(Unit)
    }

    public val state: StateFlow<SettingsState> = combine(
        _state,
        authProcessingState.observable,
        notificationToggleState.observable,
        observeSettingsPreferencesInteractor.flow,
        accountManager.isConnected,
        accountManager.activeProvider,
        uiMessageManager.message,
        userRepository.observeCurrentUser().onStart { emit(null) },
        simklLoginFlag.observe(),
        accountSwitchFlag.observe(),
        locksFlow,
    ) { currentState, isProcessingAuth, isTogglingNotifications, preferences, isLoggedIn, activeProvider, message, userProfile, simklEnabled, accountSwitchEnabled, locks ->
        val username = userProfile?.let { it.fullName ?: it.username }
        val switchTarget = resolveSwitchTarget(isLoggedIn, activeProvider, simklEnabled, accountSwitchEnabled)
        currentState.copy(
            isLoading = false,
            isUpdating = isProcessingAuth || isTogglingNotifications,
            isProcessingAuth = isProcessingAuth,
            imageQuality = preferences.imageQuality,
            theme = preferences.theme.toThemeModel(),
            openTrailersInYoutube = preferences.openTrailersInYoutube,
            includeSpecials = preferences.includeSpecials,
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
            isDebugMenuEnabled = preferences.debugMenuEnabled,
            message = message,
            locks = locks,
            currentPageTitle = resolvePageTitle(currentState.currentPage),
            rootGroups = buildRootGroups(),
            username = username,
            labels = buildLabels(
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
                if (!(action.theme.isPremium && state.value.locks.customThemesLocked)) {
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

            is BackgroundSyncToggled -> {
                coroutineScope.launch {
                    datastoreRepository.setBackgroundSyncEnabled(action.enabled)
                }
            }
            is EpisodeNotificationsToggled -> {
                if (state.value.locks.episodeNotificationsLocked) return
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

            is SettingsMessageShown -> {
                coroutineScope.launch {
                    uiMessageManager.clearMessage(action.id)
                }
            }
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
        -> SettingsPage.ROOT
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
            _state.update { it.copy(isSwitching = true) }
            runCatching { pushPendingChangesInteractor.executeSync() }
                .onFailure { logger.warning(TAG, "Pushing pending changes before switch failed: ${it.message}") }
            val count = runCatching { countUnsavedChanges() }
                .onFailure { logger.warning(TAG, "Counting unsaved changes before switch failed: ${it.message}") }
                .getOrDefault(0)
            if (count > 0) {
                _state.update {
                    it.copy(
                        isSwitching = false,
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
                executeSwitch(target)
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
        coroutineScope.launch { executeSwitch(target) }
    }

    private fun dismissSwitchDialog() {
        _state.update {
            it.copy(
                showSwitchConfirmation = false,
                pendingSwitchProvider = null,
                isSwitching = false,
                switchUnsavedCount = 0,
                switchDialogTitle = null,
                switchDialogMessage = null,
            )
        }
    }

    private suspend fun executeSwitch(target: SyncProviderSource) {
        _state.update { it.copy(isSwitching = true) }
        try {
            authManagers[target]?.launchWebView()
            withTimeoutOrNull(OAUTH_TIMEOUT) {
                accountManager.accounts.first { accounts ->
                    accounts.any { it.provider == target && it.isConnected }
                }
            } ?: error("Timed out waiting for $target sign-in")
            switchAccountInteractor.executeSync(target)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (cause: Throwable) {
            logger.error(TAG, "Account switch to $target failed: ${cause.message}")
            uiMessageManager.emitMessage(
                UiMessage(
                    message = localizer.getString(StringResourceKey.LabelAccountSwitchFailed),
                    sourceId = "AccountSwitch",
                ),
            )
        } finally {
            _state.update { it.copy(isSwitching = false) }
        }
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
        },
    )

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
        hapticFeedbackTitle = localizer.getString(StringResourceKey.SettingsHapticFeedbackTitle),
        hapticFeedbackDescription = localizer.getString(StringResourceKey.SettingsHapticFeedbackDescription),
        seasonOrderTitle = localizer.getString(StringResourceKey.SettingsSeasonOrderTitle),
        seasonOrderDescription = localizer.getString(StringResourceKey.SettingsSeasonOrderDescription),
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
        connectTitle = localizer.getString(StringResourceKey.LabelSettingsConnectTitle),
        accountSyncDescription = localizer.getString(StringResourceKey.LabelSettingsAccountSyncDescription),
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
        switchConfirm = localizer.getString(StringResourceKey.LabelAccountSwitchDialogConfirm),
        switchCancel = localizer.getString(StringResourceKey.LabelSettingsTraktDialogButtonSecondary),
        switching = localizer.getString(StringResourceKey.LabelAccountSwitching),
    )

    private companion object {
        private const val HIDDEN_TAP_THRESHOLD = 6
        private const val TAG = "SettingsPresenter"
        private val OAUTH_TIMEOUT = 2.minutes
    }
}
