package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.displayName
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAuthManager
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.UiMessageType
import com.thomaskioko.tvmaniac.data.backup.api.model.AutoBackupStatus
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupResult
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreFailure
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreResult
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreSummary
import com.thomaskioko.tvmaniac.data.backup.testing.FakeAutoBackupPreferences
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupLocationPermissions
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.api.BackupFileName
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.accountswitcher.ConnectAndSwitchProviderInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.CountUnsavedChanges
import com.thomaskioko.tvmaniac.domain.accountswitcher.PrepareAccountSwitchInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.PushPendingChangesInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.SwitchAccountInteractor
import com.thomaskioko.tvmaniac.domain.backup.BackupNowInteractor
import com.thomaskioko.tvmaniac.domain.backup.ExportBackupInteractor
import com.thomaskioko.tvmaniac.domain.backup.ObserveAutoBackupInteractor
import com.thomaskioko.tvmaniac.domain.backup.RestoreBackupInteractor
import com.thomaskioko.tvmaniac.domain.backup.RunAutoBackupInteractor
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.ObserveRewatchSupportInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObservePremiumAccessInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.settings.presenter.AccountLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.AccountLogoutClicked
import com.thomaskioko.tvmaniac.settings.presenter.AutoBackupLocationClicked
import com.thomaskioko.tvmaniac.settings.presenter.AutoBackupScheduleSelected
import com.thomaskioko.tvmaniac.settings.presenter.AutoBackupToggled
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackupDestinationCancelled
import com.thomaskioko.tvmaniac.settings.presenter.BackupDestinationSelected
import com.thomaskioko.tvmaniac.settings.presenter.BackupExportClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackupFileNameChanged
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportCancelled
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportConfirmed
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportConfirmedWithAccount
import com.thomaskioko.tvmaniac.settings.presenter.BackupNowClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackupRestoreConfirmationDialog
import com.thomaskioko.tvmaniac.settings.presenter.BackupSourceSelected
import com.thomaskioko.tvmaniac.settings.presenter.BlurUnwatchedToggled
import com.thomaskioko.tvmaniac.settings.presenter.ConfirmSwitchDiscard
import com.thomaskioko.tvmaniac.settings.presenter.DiscoverSectionToggled
import com.thomaskioko.tvmaniac.settings.presenter.DismissLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.DismissSwitchDialog
import com.thomaskioko.tvmaniac.settings.presenter.EpisodeNotificationsToggled
import com.thomaskioko.tvmaniac.settings.presenter.FontSizeChanged
import com.thomaskioko.tvmaniac.settings.presenter.HapticFeedbackToggled
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.LandscapeWidthSelected
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.PosterCornerStyleSelected
import com.thomaskioko.tvmaniac.settings.presenter.PosterStyleReset
import com.thomaskioko.tvmaniac.settings.presenter.PosterWidthSelected
import com.thomaskioko.tvmaniac.settings.presenter.QuickRateToggled
import com.thomaskioko.tvmaniac.settings.presenter.SeasonOrderToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLabelsMapper
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.ShowLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.SwitchProviderClicked
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.UpgradeToPremiumClicked
import com.thomaskioko.tvmaniac.settings.presenter.VersionClicked
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import com.thomaskioko.tvmaniac.util.testing.FakeAppMetadata
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SettingsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val accountManager = FakeAccountManager()
    private val userRepository = FakeUserRepository()
    private val fakeLogger = FakeLogger()
    private val localizer = FakeLocalizer()
    private val authManager = FakeAuthManager()
    private val simklAuthManager = FakeAuthManager(SyncProviderSource.SIMKL)
    private val simklFlag = FakeFeatureFlag(initial = false)
    private val accountSwitchFlag = FakeFeatureFlag(initial = false)
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val libraryRepository = FakeLibraryRepository()
    private val traktListRepository = FakeTraktListRepository()
    private val navigator = FakeNavigator()
    private val subscriptionManager = FakeSubscriptionManager()
    private val backupRepository = FakeBackupRepository()
    private val autoBackupPreferences = FakeAutoBackupPreferences()
    private val backupLocationPermissions = FakeBackupLocationPermissions()
    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()
        presenter = SettingsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            appMetadata = FakeAppMetadata.DEFAULT,
            datastoreRepository = datastoreRepository,
            userRepository = userRepository,
            accountManager = accountManager,
            observePremiumAccessInteractor = ObservePremiumAccessInteractor(subscriptionManager),
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            localizer = localizer,
            labelsMapper = SettingsLabelsMapper(localizer),
            logger = fakeLogger,
            authManagers = mapOf(
                SyncProviderSource.TRAKT to authManager,
                SyncProviderSource.SIMKL to simklAuthManager,
            ),
            simklLoginFlag = simklFlag,
            accountSwitchFlag = accountSwitchFlag,
            logoutInteractor = LogoutInteractor(
                accountManager = accountManager,
                userRepository = userRepository,
                datastoreRepository = datastoreRepository,
                logoutHandler = FakeLogoutHandler(),
            ),
            observeSettingsPreferencesInteractor = ObserveSettingsPreferencesInteractor(
                datastoreRepository = datastoreRepository,
                dateTimeProvider = dateTimeProvider,
            ),
            toggleEpisodeNotificationsInteractor = ToggleEpisodeNotificationsInteractor(
                datastoreRepository = datastoreRepository,
            ),
            navigator = navigator,
            prepareAccountSwitchInteractor = PrepareAccountSwitchInteractor(
                pushPendingChangesInteractor = PushPendingChangesInteractor(
                    watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                    libraryRepository = libraryRepository,
                ),
                countUnsavedChanges = CountUnsavedChanges(
                    libraryRepository = libraryRepository,
                    watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                    traktListRepository = traktListRepository,
                ),
                logger = fakeLogger,
            ),
            connectAndSwitchProviderInteractor = ConnectAndSwitchProviderInteractor(
                authManagers = mapOf(
                    SyncProviderSource.TRAKT to authManager,
                    SyncProviderSource.SIMKL to simklAuthManager,
                ),
                accountManager = accountManager,
                switchAccountInteractor = SwitchAccountInteractor(
                    logoutHandler = FakeLogoutHandler(),
                    accountManager = accountManager,
                    resyncProfile = {},
                    resyncLibrary = {},
                    resyncContinueWatching = {},
                    appScopeLauncher = FakeAppScopeLauncher(TestScope(testDispatcher)),
                ),
            ),
            observeRewatchSupportInteractor = ObserveRewatchSupportInteractor(
                accountManager = accountManager,
                rewatchRepository = FakeRewatchRepository(),
            ),
            exportBackupInteractor = ExportBackupInteractor(backupRepository),
            restoreBackupInteractor = RestoreBackupInteractor(backupRepository, FakeBackgroundTaskScheduler()),
            backupNowInteractor = BackupNowInteractor(
                RunAutoBackupInteractor(
                    backupRepository = backupRepository,
                    datastoreRepository = datastoreRepository,
                    autoBackupPreferences = autoBackupPreferences,
                    dateTimeProvider = dateTimeProvider,
                    logger = fakeLogger,
                ),
            ),
            backupLocationPermissions = backupLocationPermissions,
            observeAutoBackupInteractor = ObserveAutoBackupInteractor(
                datastoreRepository = datastoreRepository,
                autoBackupPreferences = autoBackupPreferences,
                dateTimeProvider = dateTimeProvider,
            ),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit default state when initialized`() = runTest {
        presenter.state.test {
            val state = awaitItem()
            state.versionName shouldBe "0.0.0"
        }
    }

    @Test
    fun `should update theme when theme is selected`() = runTest {
        presenter.state.test {
            val initialState = awaitItem()
            initialState.versionName shouldBe "0.0.0"
            initialState.theme shouldBe ThemeModel.SYSTEM

            presenter.dispatch(ThemeSelected(ThemeModel.DARK))

            awaitItem().theme shouldBe ThemeModel.DARK
        }
    }

    @Test
    fun `should persist and reflect haptic feedback given the toggle is flipped`() = runTest {
        presenter.state.test {
            awaitItem().hapticFeedbackEnabled shouldBe true

            presenter.dispatch(HapticFeedbackToggled(false))

            awaitItem().hapticFeedbackEnabled shouldBe false
            datastoreRepository.observeHapticFeedbackEnabled().first() shouldBe false
        }
    }

    @Test
    fun `should persist and reflect season order given the toggle is flipped`() = runTest {
        presenter.state.test {
            awaitItem().newestSeasonFirst shouldBe false

            presenter.dispatch(SeasonOrderToggled(true))

            awaitItem().newestSeasonFirst shouldBe true
            datastoreRepository.observeSeasonSortOrder().first() shouldBe SeasonSortOrder.NEWEST_FIRST
        }
    }

    @Test
    fun `should persist and reflect blur unwatched given the toggle is flipped`() = runTest {
        presenter.state.test {
            awaitItem().blurImage shouldBe false

            presenter.dispatch(BlurUnwatchedToggled(true))

            awaitItem().blurImage shouldBe true
            datastoreRepository.observeBlurUnwatchedEpisodeImages().first() shouldBe true
        }
    }

    @Test
    fun `should persist and hide discover section given the toggle is flipped`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.discoverSectionToggles.isEmpty()) {
                state = awaitItem()
            }
            state.discoverSectionToggles.single { it.section == DiscoverSection.POPULAR }.visible shouldBe true

            presenter.dispatch(DiscoverSectionToggled(DiscoverSection.POPULAR, visible = false))

            var updated = awaitItem()
            while (updated.discoverSectionToggles.single { it.section == DiscoverSection.POPULAR }.visible) {
                updated = awaitItem()
            }
            datastoreRepository.observeHiddenDiscoverSections().first() shouldBe setOf(DiscoverSection.POPULAR)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should persist and reflect font size given the slider changes`() = runTest {
        presenter.state.test {
            awaitItem().fontSizePercent shouldBe 100

            presenter.dispatch(FontSizeChanged(120))

            awaitItem().fontSizePercent shouldBe 120
            datastoreRepository.observeFontSizePercent().first() shouldBe 120
        }
    }

    @Test
    fun `should persist and reflect poster style selections while unlocked`() = runTest {
        presenter.state.test {
            val initial = awaitItem()
            initial.posterWidth shouldBe PosterWidth.STANDARD
            initial.landscapeWidth shouldBe PosterWidth.STANDARD
            initial.posterCornerStyle shouldBe PosterCornerStyle.SHARP

            presenter.dispatch(PosterWidthSelected(PosterWidth.LARGE))
            awaitItem().posterWidth shouldBe PosterWidth.LARGE

            presenter.dispatch(LandscapeWidthSelected(PosterWidth.COMPACT))
            awaitItem().landscapeWidth shouldBe PosterWidth.COMPACT

            presenter.dispatch(PosterCornerStyleSelected(PosterCornerStyle.PILL))
            awaitItem().posterCornerStyle shouldBe PosterCornerStyle.PILL

            datastoreRepository.observePosterWidth().first() shouldBe PosterWidth.LARGE
            datastoreRepository.observeLandscapeWidth().first() shouldBe PosterWidth.COMPACT
            datastoreRepository.observePosterCornerStyle().first() shouldBe PosterCornerStyle.PILL
        }
    }

    @Test
    fun `should restore poster style defaults given reset`() = runTest {
        datastoreRepository.savePosterWidth(PosterWidth.LARGE)
        datastoreRepository.saveLandscapeWidth(PosterWidth.COMPACT)
        datastoreRepository.savePosterCornerStyle(PosterCornerStyle.PILL)

        presenter.state.test {
            awaitItem()

            presenter.dispatch(PosterStyleReset)
            testScheduler.advanceUntilIdle()

            datastoreRepository.observePosterWidth().first() shouldBe PosterWidth.STANDARD
            datastoreRepository.observeLandscapeWidth().first() shouldBe PosterWidth.STANDARD
            datastoreRepository.observePosterCornerStyle().first() shouldBe PosterCornerStyle.SHARP
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should ignore poster style selections while locked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.CustomThemes, false)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(PosterWidthSelected(PosterWidth.LARGE))
        presenter.dispatch(LandscapeWidthSelected(PosterWidth.COMPACT))
        presenter.dispatch(PosterCornerStyleSelected(PosterCornerStyle.SHARP))
        testScheduler.advanceUntilIdle()

        datastoreRepository.observePosterWidth().first() shouldBe PosterWidth.STANDARD
        datastoreRepository.observeLandscapeWidth().first() shouldBe PosterWidth.STANDARD
        datastoreRepository.observePosterCornerStyle().first() shouldBe PosterCornerStyle.SHARP
    }

    @Test
    fun `should show and hide trakt dialog when toggled`() = runTest {
        presenter.state.test {
            awaitItem()

            presenter.dispatch(ShowLogoutDialog)
            awaitItem().showLogoutConfirmation shouldBe true

            presenter.dispatch(DismissLogoutDialog)
            awaitItem().showLogoutConfirmation shouldBe false
        }
    }

    @Test
    fun `should update image quality when quality is selected`() = runTest {
        presenter.state.test {
            awaitItem()

            presenter.dispatch(ImageQualitySelected(ImageQuality.HIGH))
            awaitItem().imageQuality shouldBe ImageQuality.HIGH

            presenter.dispatch(ImageQualitySelected(ImageQuality.LOW))
            awaitItem().imageQuality shouldBe ImageQuality.LOW
        }
    }

    @Test
    fun `should include version name in state`() = runTest {
        presenter.state.test {
            val state = awaitItem()
            state.versionName shouldBe "0.0.0"
        }
    }

    @Test
    fun `should open sub page when page is selected`() = runTest {
        presenter.state.test {
            awaitItem().currentPage shouldBe SettingsPage.ROOT

            presenter.dispatch(OpenSettingsPage(SettingsPage.APPEARANCE))
            awaitItem().currentPage shouldBe SettingsPage.APPEARANCE
        }
    }

    @Test
    fun `should return to root when back is clicked on a sub page`() = runTest {
        presenter.state.test {
            awaitItem().currentPage shouldBe SettingsPage.ROOT

            presenter.dispatch(OpenSettingsPage(SettingsPage.BEHAVIOR))
            awaitItem().currentPage shouldBe SettingsPage.BEHAVIOR

            presenter.dispatch(BackClicked)
            awaitItem().currentPage shouldBe SettingsPage.ROOT
        }
    }

    @Test
    fun `should remain on root when back is clicked on root`() = runTest {
        presenter.state.test {
            awaitItem().currentPage shouldBe SettingsPage.ROOT

            presenter.dispatch(BackClicked)
            expectNoEvents()
        }
    }

    @Test
    fun `should open the layout page given the layout entry is selected`() = runTest {
        presenter.state.test {
            awaitItem().currentPage shouldBe SettingsPage.ROOT

            presenter.dispatch(OpenSettingsPage(SettingsPage.LAYOUT))
            awaitItem().currentPage shouldBe SettingsPage.LAYOUT
        }
    }

    @Test
    fun `should return to root when back is clicked from the layout page`() = runTest {
        presenter.state.test {
            awaitItem().currentPage shouldBe SettingsPage.ROOT

            presenter.dispatch(OpenSettingsPage(SettingsPage.LAYOUT))
            awaitItem().currentPage shouldBe SettingsPage.LAYOUT

            presenter.dispatch(BackClicked)
            awaitItem().currentPage shouldBe SettingsPage.ROOT
        }
    }

    @Test
    fun `should include the layout entry in the root groups`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.rootGroups.isEmpty()) {
                state = awaitItem()
            }
            state.rootGroups.flatMap { it.items }.any { it.page == SettingsPage.LAYOUT } shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should resolve connect prompt labels when logged out`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.labels.login.isEmpty()) {
                state = awaitItem()
            }

            state.isAuthenticated shouldBe false
            state.activeProvider shouldBe null
            state.labels.traktConnected shouldBe localizer.getString(StringResourceKey.LabelSettingsTraktConnect)
            state.labels.traktConnectedDescription shouldBe
                localizer.getString(StringResourceKey.SettingsTraktDetailDescription)
            state.labels.login shouldBe localizer.getString(StringResourceKey.Login)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should resolve connected labels when logged in`() = runTest {
        presenter.state.test {
            awaitItem()
            accountManager.setActiveProvider(SyncProviderSource.TRAKT)

            var state = awaitItem()
            while (!state.isAuthenticated) {
                state = awaitItem()
            }

            state.activeProvider shouldBe SyncProviderSource.TRAKT
            state.labels.traktConnected shouldBe
                localizer.getString(StringResourceKey.LabelSettingsTraktConnectedAs, "Test User")
            state.labels.traktConnectedDescription shouldBe
                localizer.getString(StringResourceKey.LabelSettingsTraktConnectedDescription)
            state.labels.logout shouldBe localizer.getString(StringResourceKey.Logout)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should launch web view when login is clicked`() = runTest {
        var launched = false
        authManager.setOnLaunchWebView { launched = true }

        presenter.dispatch(AccountLoginClicked(SyncProviderSource.TRAKT))
        testScheduler.advanceUntilIdle()

        launched shouldBe true
    }

    @Test
    fun `should launch the chosen provider given a non default provider`() = runTest {
        var traktLaunched = false
        var simklLaunched = false
        authManager.setOnLaunchWebView { traktLaunched = true }
        simklAuthManager.setOnLaunchWebView { simklLaunched = true }

        presenter.dispatch(AccountLoginClicked(SyncProviderSource.SIMKL))
        testScheduler.advanceUntilIdle()

        simklLaunched shouldBe true
        traktLaunched shouldBe false
    }

    @Test
    fun `should log out the active provider given logout is clicked`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)

        presenter.dispatch(AccountLogoutClicked)
        testScheduler.advanceUntilIdle()

        accountManager.lastLogoutProvider shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should expose only the trakt option given the simkl flag is off`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.authProviders.isEmpty()) {
                state = awaitItem()
            }
            state.authProviders.map { it.provider } shouldBe listOf(SyncProviderSource.TRAKT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should expose both provider options given the simkl flag is on`() = runTest {
        simklFlag.value = true
        presenter.state.test {
            var state = awaitItem()
            while (state.authProviders.size < 2) {
                state = awaitItem()
            }
            state.authProviders.map { it.provider } shouldBe listOf(SyncProviderSource.TRAKT, SyncProviderSource.SIMKL)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show the account row when logged out`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.rootGroups.isEmpty()) {
                state = awaitItem()
            }
            state.isAuthenticated shouldBe false
            state.rootGroups.flatMap { it.items }.any { it.page == SettingsPage.ACCOUNT } shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should resolve the connected description for the active provider`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        presenter.state.test {
            var state = awaitItem()
            while (state.accountConnectedDescription == null) {
                state = awaitItem()
            }
            state.accountConnectedDescription shouldBe
                localizer.getString(StringResourceKey.LabelSettingsSimklConnectedDescription)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should switch to the new provider given no unsaved changes`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        presenter.dispatch(SwitchProviderClicked(SyncProviderSource.SIMKL))
        testScheduler.runCurrent()

        accountManager.setAccounts(
            listOf(
                ConnectedAccount(provider = SyncProviderSource.TRAKT, isConnected = true),
                ConnectedAccount(provider = SyncProviderSource.SIMKL, isConnected = true),
            ),
        )
        testScheduler.runCurrent()

        accountManager.lastLogoutProvider shouldBe SyncProviderSource.TRAKT
        accountManager.getActiveProvider() shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should park at confirmation given unsaved changes remain`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        libraryRepository.setPendingFollowedShowsCount(3L)

        presenter.state.test {
            awaitItem()
            presenter.dispatch(SwitchProviderClicked(SyncProviderSource.SIMKL))

            var state = awaitItem()
            while (!state.showSwitchConfirmation) {
                state = awaitItem()
            }
            state.switchUnsavedCount shouldBe 3
            state.pendingSwitchProvider shouldBe SyncProviderSource.SIMKL
            cancelAndIgnoreRemainingEvents()
        }
        accountManager.lastLogoutProvider shouldBe null
    }

    @Test
    fun `should abort the switch given the confirmation is dismissed`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        libraryRepository.setPendingFollowedShowsCount(2L)

        presenter.state.test {
            awaitItem()
            presenter.dispatch(SwitchProviderClicked(SyncProviderSource.SIMKL))
            var state = awaitItem()
            while (!state.showSwitchConfirmation) {
                state = awaitItem()
            }

            presenter.dispatch(DismissSwitchDialog)
            var dismissed = awaitItem()
            while (dismissed.showSwitchConfirmation) {
                dismissed = awaitItem()
            }
            cancelAndIgnoreRemainingEvents()
        }
        accountManager.lastLogoutProvider shouldBe null
        accountManager.getActiveProvider() shouldBe SyncProviderSource.TRAKT
    }

    @Test
    fun `should proceed with the switch given the user confirms discard`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        libraryRepository.setPendingFollowedShowsCount(1L)

        presenter.dispatch(SwitchProviderClicked(SyncProviderSource.SIMKL))
        testScheduler.runCurrent()

        presenter.dispatch(ConfirmSwitchDiscard)
        testScheduler.runCurrent()

        accountManager.setAccounts(
            listOf(
                ConnectedAccount(provider = SyncProviderSource.TRAKT, isConnected = true),
                ConnectedAccount(provider = SyncProviderSource.SIMKL, isConnected = true),
            ),
        )
        testScheduler.runCurrent()

        accountManager.lastLogoutProvider shouldBe SyncProviderSource.TRAKT
        accountManager.getActiveProvider() shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should expose the switch target given the account switch flag is on`() = runTest {
        simklFlag.value = true
        accountSwitchFlag.value = true
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        presenter.state.test {
            var state = awaitItem()
            while (state.switchTargetProvider == null) {
                state = awaitItem()
            }
            state.switchTargetProvider shouldBe SyncProviderSource.SIMKL
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should hide the switch target given the account switch flag is off`() = runTest {
        simklFlag.value = true
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        presenter.state.test {
            var state = awaitItem()
            while (!state.isAuthenticated) {
                state = awaitItem()
            }
            state.switchTargetProvider shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to the debug menu on a single tap given it is already enabled`() = runTest {
        datastoreRepository.setDebugMenuEnabled(true)

        presenter.state.test {
            var state = awaitItem()
            while (!state.isDebugMenuEnabled) {
                state = awaitItem()
            }

            presenter.dispatch(VersionClicked)

            cancelAndIgnoreRemainingEvents()
        }

        navigator.lastNavigatedRoute shouldBe DebugRoute
    }

    @Test
    fun `should persist and navigate given the version is tapped to the threshold`() = runTest {
        repeat(6) { presenter.dispatch(VersionClicked) }
        testScheduler.advanceUntilIdle()

        navigator.lastNavigatedRoute shouldBe DebugRoute
        datastoreRepository.observeDebugMenuEnabled().first() shouldBe true
    }

    @Test
    fun `should not navigate given the version is tapped fewer than the threshold`() = runTest {
        repeat(5) { presenter.dispatch(VersionClicked) }
        testScheduler.advanceUntilIdle()

        navigator.lastNavigatedRoute shouldBe null
        datastoreRepository.observeDebugMenuEnabled().first() shouldBe false
    }

    @Test
    fun `should report unlocked locks given full access`() = runTest {
        presenter.state.test {
            testScheduler.advanceUntilIdle()
            val premium = expectMostRecentItem().premium
            premium.customThemesLocked shouldBe false
            premium.posterStyleLocked shouldBe false
            premium.episodeNotificationsLocked shouldBe false
            premium.quickRateLocked shouldBe false
        }
    }

    @Test
    fun `should surface locks given subscription access is revoked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.CustomThemes, false)
        subscriptionManager.setAccess(SubscriptionFeature.EpisodeNotifications, false)
        subscriptionManager.setAccess(SubscriptionFeature.QuickRate, false)

        presenter.state.test {
            testScheduler.advanceUntilIdle()
            val premium = expectMostRecentItem().premium
            premium.customThemesLocked shouldBe true
            premium.posterStyleLocked shouldBe true
            premium.episodeNotificationsLocked shouldBe true
            premium.quickRateLocked shouldBe true
            premium.badgeText shouldBe localizer.getString(StringResourceKey.LabelPremiumBadge)
            premium.upgradeText shouldBe localizer.getString(StringResourceKey.LabelUpgradeToPremium)
        }
    }

    @Test
    fun `should ignore ThemeSelected for a premium palette while locked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.CustomThemes, false)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(ThemeSelected(ThemeModel.TERMINAL))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().theme shouldBe ThemeModel.SYSTEM
        }
    }

    @Test
    fun `should apply a free palette while custom themes are locked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.CustomThemes, false)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(ThemeSelected(ThemeModel.DARK))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().theme shouldBe ThemeModel.DARK
        }
    }

    @Test
    fun `should ignore EpisodeNotificationsToggled while locked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.EpisodeNotifications, false)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(EpisodeNotificationsToggled(true))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().episodeNotificationsEnabled shouldBe false
        }
        datastoreRepository.observeEpisodeNotificationsEnabled().first() shouldBe false
    }

    @Test
    fun `should enable quick rate given the toggle is switched on`() = runTest {
        presenter.dispatch(QuickRateToggled(true))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().quickRateEnabled shouldBe true
        }
        datastoreRepository.observeQuickRateEnabled().first() shouldBe true
    }

    @Test
    fun `should disable quick rate given the toggle is switched off`() = runTest {
        presenter.dispatch(QuickRateToggled(true))
        testScheduler.advanceUntilIdle()

        presenter.dispatch(QuickRateToggled(false))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().quickRateEnabled shouldBe false
        }
        datastoreRepository.observeQuickRateEnabled().first() shouldBe false
    }

    @Test
    fun `should ignore QuickRateToggled while locked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.QuickRate, false)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(QuickRateToggled(true))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().quickRateEnabled shouldBe false
        }
        datastoreRepository.observeQuickRateEnabled().first() shouldBe false
    }

    @Test
    fun `should apply a premium palette when custom themes are unlocked`() = runTest {
        presenter.dispatch(ThemeSelected(ThemeModel.TERMINAL))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().theme shouldBe ThemeModel.TERMINAL
        }
    }

    @Test
    fun `should keep state unchanged given UpgradeToPremiumClicked is dispatched`() = runTest {
        presenter.dispatch(UpgradeToPremiumClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().theme shouldBe ThemeModel.SYSTEM
        }
    }

    @Test
    fun `should show the backup row given settings load`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val state = expectMostRecentItem()
            state.rootGroups.flatMap { it.items }.any { it.page == SettingsPage.BACKUP } shouldBe true
        }
    }

    @Test
    fun `should report the backup page locked given access is denied`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.CloudBackup, false)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().premium.backupLocked shouldBe true
        }
    }

    @Test
    fun `should ask for a destination given export is tapped`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupExportClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.awaitingDestination shouldBe true
        }
    }

    @Test
    fun `should stop asking for a destination given the picker is dismissed`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupExportClicked)
        presenter.dispatch(BackupDestinationCancelled)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.awaitingDestination shouldBe false
        }
    }

    @Test
    fun `should write a backup given a destination is chosen`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupDestinationSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        backupRepository.lastWriteLocation shouldBe "$LOCATION/$FILE_NAME"

        presenter.state.test {
            val state = expectMostRecentItem()
            state.backup.isExporting shouldBe false
            state.message.shouldNotBeNull().type shouldBe UiMessageType.Success
        }
    }

    @Test
    fun `should report a failure given the backup cannot be written`() = runTest {
        testScheduler.advanceUntilIdle()
        backupRepository.setWriteResult(BackupResult.Failed(BackupFailure.WriteFailed))

        presenter.dispatch(BackupDestinationSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().message.shouldNotBeNull()
        }
    }

    @Test
    fun `should include the backup labels given the state is first read`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val backup = expectMostRecentItem().backup
            backup.exportTitle shouldBe localizer.getString(StringResourceKey.SettingsBackupExportTitle)
            backup.exportDescription shouldBe localizer.getString(StringResourceKey.SettingsBackupExportDescription)
            backup.importTitle shouldBe localizer.getString(StringResourceKey.SettingsBackupImportTitle)
            backup.importDescription shouldBe localizer.getString(StringResourceKey.SettingsBackupImportDescription)
        }
    }

    @Test
    fun `should include the automatic backup labels given the state is first read`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val autoBackup = expectMostRecentItem().backup.autoBackup
            autoBackup.title shouldBe localizer.getString(StringResourceKey.SettingsAutoBackupTitle)
            autoBackup.description shouldBe localizer.getString(StringResourceKey.SettingsAutoBackupDescription)
            autoBackup.scheduleTitle shouldBe localizer.getString(StringResourceKey.SettingsAutoBackupScheduleTitle)
            autoBackup.backupNowTitle shouldBe localizer.getString(StringResourceKey.SettingsAutoBackupNowTitle)
        }
    }

    @Test
    fun `should report no backup yet given none has run`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val autoBackup = expectMostRecentItem().backup.autoBackup
            autoBackup.lastRunLabel shouldBe
                localizer.getString(StringResourceKey.SettingsAutoBackupLastRunNever)
            autoBackup.failureWarning.shouldBeNull()
        }
    }

    @Test
    fun `should report when the last backup ran given one has`() = runTest {
        autoBackupPreferences.setStatus(AutoBackupStatus(lastRunAt = NOW, lastRunFailed = false))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val autoBackup = expectMostRecentItem().backup.autoBackup
            autoBackup.lastRunLabel shouldBe localizer.getString(
                StringResourceKey.SettingsAutoBackupLastRun,
                dateTimeProvider.epochToDisplayDateTime(NOW),
            )
            autoBackup.failureWarning.shouldBeNull()
        }
    }

    @Test
    fun `should warn given the last backup failed`() = runTest {
        autoBackupPreferences.setStatus(AutoBackupStatus(lastRunAt = NOW, lastRunFailed = true))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.autoBackup.failureWarning shouldBe
                localizer.getString(StringResourceKey.SettingsAutoBackupLastRunFailed)
        }
    }

    @Test
    fun `should turn automatic backup on given it is toggled`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(AutoBackupToggled(enabled = true))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.autoBackup.enabled shouldBe true
        }
    }

    @Test
    fun `should mark the chosen schedule as selected given one is picked`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(AutoBackupScheduleSelected(AutoBackupInterval.MONTHLY))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val autoBackup = expectMostRecentItem().backup.autoBackup
            autoBackup.scheduleLabel shouldBe
                localizer.getString(StringResourceKey.SettingsAutoBackupScheduleMonthly)
            autoBackup.scheduleOptions.single { it.selected }.interval shouldBe AutoBackupInterval.MONTHLY
        }
    }

    @Test
    fun `should offer every schedule given the options are read`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val options = expectMostRecentItem().backup.autoBackup.scheduleOptions
            options.map { it.interval } shouldBe AutoBackupInterval.entries
            options.single { it.selected }.interval shouldBe AutoBackupInterval.WEEKLY
        }
    }

    @Test
    fun `should save the chosen location given it is picked for automatic backup`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(AutoBackupLocationClicked)
        testScheduler.advanceUntilIdle()
        presenter.dispatch(BackupDestinationSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFolder() shouldBe LOCATION
        backupLocationPermissions.requested() shouldBe listOf(LOCATION)
        backupRepository.lastWriteLocation.shouldBeNull()

        presenter.state.test {
            val autoBackup = expectMostRecentItem().backup.autoBackup
            autoBackup.locationLabel shouldBe LOCATION
            autoBackup.hasLocation shouldBe true
        }
    }

    @Test
    fun `should show the file name given a location is saved`() = runTest {
        backupLocationPermissions.setDisplayName("tvmaniac-backup.json")
        testScheduler.advanceUntilIdle()

        presenter.dispatch(AutoBackupLocationClicked)
        testScheduler.advanceUntilIdle()
        presenter.dispatch(BackupDestinationSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.autoBackup.locationLabel shouldBe "tvmaniac-backup.json"
        }
    }

    @Test
    fun `should keep no location given write access cannot be kept`() = runTest {
        testScheduler.advanceUntilIdle()
        backupLocationPermissions.setPersisted(false)

        presenter.dispatch(AutoBackupLocationClicked)
        testScheduler.advanceUntilIdle()
        presenter.dispatch(BackupDestinationSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFolder().shouldBeNull()

        presenter.state.test {
            expectMostRecentItem().message.shouldNotBeNull()
        }
    }

    @Test
    fun `should save the file name given a usable one is entered`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupFileNameChanged("my shows"))
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFileName() shouldBe "my shows.json"

        presenter.state.test {
            expectMostRecentItem().backup.autoBackup.fileName shouldBe "my shows.json"
        }
    }

    @Test
    fun `should refuse the file name given it cannot be used`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupFileNameChanged("nested/name"))
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFileName() shouldBe BackupFileName.Default

        presenter.state.test {
            expectMostRecentItem().message.shouldNotBeNull().message shouldBe
                localizer.getString(StringResourceKey.ErrorBackupFileNameInvalid)
        }
    }

    @Test
    fun `should write a backup given back up now is tapped`() = runTest {
        testScheduler.advanceUntilIdle()
        datastoreRepository.saveBackupFolder(LOCATION)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupNowClicked)
        testScheduler.advanceUntilIdle()

        backupRepository.lastWriteLocation shouldBe "$LOCATION/$FILE_NAME"

        presenter.state.test {
            expectMostRecentItem().backup.autoBackup.isBackingUp shouldBe false
        }
    }

    @Test
    fun `should ask for confirmation given restore is tapped`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val confirm = expectMostRecentItem().backup.confirm.shouldNotBeNull()
            confirm.message shouldBe localizer.getString(StringResourceKey.SettingsBackupRestoreConfirmMessage)
        }
    }

    @Test
    fun `should name the provider in the confirmation given an account is connected`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val confirm = expectMostRecentItem().backup.confirm.shouldNotBeNull()
            confirm.message shouldBe localizer.getString(
                StringResourceKey.SettingsBackupRestoreConfirmMessageConnected,
                SyncProviderSource.TRAKT.displayName,
            )
        }
    }

    @Test
    fun `should offer the account choice given an account is connected`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val confirm = expectMostRecentItem().backup.confirm
                .shouldBeInstanceOf<BackupRestoreConfirmationDialog.Connected>()
            confirm.accountLabel shouldBe localizer.getString(
                StringResourceKey.SettingsBackupRestoreConfirmAccountButton,
                SyncProviderSource.TRAKT.displayName,
            )
            confirm.deviceLabel shouldBe
                localizer.getString(StringResourceKey.SettingsBackupRestoreConfirmDeviceButton)
        }
    }

    @Test
    fun `should resolve the provider name given an account is connected`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().activeProviderName shouldBe SyncProviderSource.SIMKL.displayName
        }
    }

    @Test
    fun `should resolve no provider name given user is signed out`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().activeProviderName.shouldBeNull()
        }
    }

    @Test
    fun `should name simkl in the account choice given simkl is connected`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val confirm = expectMostRecentItem().backup.confirm
                .shouldBeInstanceOf<BackupRestoreConfirmationDialog.Connected>()
            confirm.accountLabel shouldBe localizer.getString(
                StringResourceKey.SettingsBackupRestoreConfirmAccountButton,
                SyncProviderSource.SIMKL.displayName,
            )
        }
    }

    @Test
    fun `should offer no account choice given user is signed out`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportClicked)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.confirm.shouldBeInstanceOf<BackupRestoreConfirmationDialog.Local>()
        }
    }

    @Test
    fun `should restore to the account given the account choice was taken`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportConfirmedWithAccount)
        presenter.dispatch(BackupSourceSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        backupRepository.lastRestoreSyncedWithConnectedAccount shouldBe true
    }

    @Test
    fun `should restore on the device only given the device choice was taken`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportConfirmed)
        presenter.dispatch(BackupSourceSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        backupRepository.lastRestoreSyncedWithConnectedAccount shouldBe false
    }

    @Test
    fun `should forget the account choice given the confirmation is cancelled`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportConfirmedWithAccount)
        presenter.dispatch(BackupImportCancelled)
        presenter.dispatch(BackupSourceSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        backupRepository.lastRestoreSyncedWithConnectedAccount shouldBe false
    }

    @Test
    fun `should change nothing given the confirmation is cancelled`() = runTest {
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupImportClicked)
        presenter.dispatch(BackupImportCancelled)
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val backup = expectMostRecentItem().backup
            backup.confirm.shouldBeNull()
            backup.awaitingSource shouldBe false
        }
        backupRepository.lastRestoreLocation shouldBe null
    }

    @Test
    fun `should report the counts given a backup is restored`() = runTest {
        backupRepository.setRestoreResult(
            RestoreResult.Restored(
                RestoreSummary(
                    showCount = 3,
                    episodeCount = 42,
                    skippedShows = listOf("Dark"),
                    rewatchSessionsKept = 2,
                ),
            ),
        )
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupSourceSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val summary = expectMostRecentItem().backup.summary.shouldNotBeNull()
            summary.showsRestored shouldBe localizer.getPlural(PluralsResourceKey.BackupShowsRestored, 3, 3)
            summary.episodesRestored shouldBe localizer.getPlural(PluralsResourceKey.BackupEpisodesRestored, 42, 42)
            summary.skippedShows shouldBe listOf("Dark")
            summary.rewatchNotice.shouldNotBeNull()
        }
    }

    @Test
    fun `should omit the rewatch notice given no sessions were kept`() = runTest {
        backupRepository.setRestoreResult(
            RestoreResult.Restored(RestoreSummary(showCount = 1, episodeCount = 1)),
        )
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupSourceSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().backup.summary.shouldNotBeNull().rewatchNotice.shouldBeNull()
        }
    }

    @Test
    fun `should report a message given a sync is running`() = runTest {
        backupRepository.setRestoreResult(RestoreResult.Failed(RestoreFailure.SyncInProgress))
        testScheduler.advanceUntilIdle()

        presenter.dispatch(BackupSourceSelected(LOCATION))
        testScheduler.advanceUntilIdle()

        presenter.state.test {
            val state = expectMostRecentItem()
            state.message.shouldNotBeNull()
            state.backup.summary.shouldBeNull()
        }
    }

    private companion object {
        private const val FILE_NAME = "tvmaniac-backup.json"
        private const val LOCATION = "content://downloads/tvmaniac-backup.json"
        private const val NOW = 1_700_000_000_000L
    }
}
