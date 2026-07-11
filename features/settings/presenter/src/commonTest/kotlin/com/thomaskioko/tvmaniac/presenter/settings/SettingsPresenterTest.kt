package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAuthManager
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.accountswitcher.CountUnsavedChanges
import com.thomaskioko.tvmaniac.domain.accountswitcher.PushPendingChangesInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.SwitchAccountInteractor
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.settings.presenter.AccountLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.AccountLogoutClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.BlurUnwatchedToggled
import com.thomaskioko.tvmaniac.settings.presenter.ConfirmSwitchDiscard
import com.thomaskioko.tvmaniac.settings.presenter.DismissLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.DismissSwitchDialog
import com.thomaskioko.tvmaniac.settings.presenter.EpisodeNotificationsToggled
import com.thomaskioko.tvmaniac.settings.presenter.HapticFeedbackToggled
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SeasonOrderToggled
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
import io.kotest.matchers.shouldBe
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
    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = SettingsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            appMetadata = FakeAppMetadata.DEFAULT,
            datastoreRepository = datastoreRepository,
            userRepository = userRepository,
            accountManager = accountManager,
            subscriptionManager = subscriptionManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            localizer = localizer,
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
            pushPendingChangesInteractor = PushPendingChangesInteractor(
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                libraryRepository = libraryRepository,
            ),
            countUnsavedChanges = CountUnsavedChanges(
                libraryRepository = libraryRepository,
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                traktListRepository = traktListRepository,
            ),
            switchAccountInteractor = SwitchAccountInteractor(
                logoutHandler = FakeLogoutHandler(),
                accountManager = accountManager,
                resyncProfile = {},
                resyncLibrary = {},
                resyncContinueWatching = {},
                appScopeLauncher = FakeAppScopeLauncher(TestScope(testDispatcher)),
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
            val locks = expectMostRecentItem().locks
            locks.customThemesLocked shouldBe false
            locks.episodeNotificationsLocked shouldBe false
        }
    }

    @Test
    fun `should surface locks given subscription access is revoked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.CustomThemes, false)
        subscriptionManager.setAccess(SubscriptionFeature.EpisodeNotifications, false)

        presenter.state.test {
            testScheduler.advanceUntilIdle()
            val locks = expectMostRecentItem().locks
            locks.customThemesLocked shouldBe true
            locks.episodeNotificationsLocked shouldBe true
            locks.badgeText shouldBe localizer.getString(StringResourceKey.LabelPremiumBadge)
            locks.upgradeText shouldBe localizer.getString(StringResourceKey.LabelUpgradeToPremium)
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
}
