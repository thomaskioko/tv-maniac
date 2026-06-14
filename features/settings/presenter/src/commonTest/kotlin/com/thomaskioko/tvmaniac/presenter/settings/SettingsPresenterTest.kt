package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAuthManager
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.accountswitcher.CountUnsavedChanges
import com.thomaskioko.tvmaniac.domain.accountswitcher.PushPendingChangesInteractor
import com.thomaskioko.tvmaniac.domain.accountswitcher.ResyncContinueWatching
import com.thomaskioko.tvmaniac.domain.accountswitcher.ResyncLibrary
import com.thomaskioko.tvmaniac.domain.accountswitcher.SwitchAccountInteractor
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.settings.presenter.AccountLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.AccountLogoutClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.ConfirmSwitchDiscard
import com.thomaskioko.tvmaniac.settings.presenter.DismissLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.DismissSwitchDialog
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.ShowLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.SwitchProviderClicked
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import com.thomaskioko.tvmaniac.util.testing.FakeAppMetadata
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
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
    private val simklAuthManager = FakeAuthManager(AccountProvider.SIMKL)
    private val simklFlag = FakeFeatureFlag(initial = false)
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val libraryRepository = FakeLibraryRepository()
    private val traktListRepository = FakeTraktListRepository()
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
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            localizer = localizer,
            logger = fakeLogger,
            authManagers = mapOf(
                AccountProvider.TRAKT to authManager,
                AccountProvider.SIMKL to simklAuthManager,
            ),
            simklLoginFlag = simklFlag,
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
            navigator = NoOpNavigator(),
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
                resyncLibrary = ResyncLibrary {},
                resyncContinueWatching = ResyncContinueWatching {},
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
            accountManager.setActiveProvider(AccountProvider.TRAKT)

            var state = awaitItem()
            while (!state.isAuthenticated) {
                state = awaitItem()
            }

            state.activeProvider shouldBe AccountProvider.TRAKT
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

        presenter.dispatch(AccountLoginClicked(AccountProvider.TRAKT))
        testScheduler.advanceUntilIdle()

        launched shouldBe true
    }

    @Test
    fun `should launch the chosen provider given a non default provider`() = runTest {
        var traktLaunched = false
        var simklLaunched = false
        authManager.setOnLaunchWebView { traktLaunched = true }
        simklAuthManager.setOnLaunchWebView { simklLaunched = true }

        presenter.dispatch(AccountLoginClicked(AccountProvider.SIMKL))
        testScheduler.advanceUntilIdle()

        simklLaunched shouldBe true
        traktLaunched shouldBe false
    }

    @Test
    fun `should log out the active provider given logout is clicked`() = runTest {
        accountManager.setActiveProvider(AccountProvider.SIMKL)

        presenter.dispatch(AccountLogoutClicked)
        testScheduler.advanceUntilIdle()

        accountManager.lastLogoutProvider shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should expose only the trakt option given the simkl flag is off`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.authProviders.isEmpty()) {
                state = awaitItem()
            }
            state.authProviders.map { it.provider } shouldBe listOf(AccountProvider.TRAKT)
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
            state.authProviders.map { it.provider } shouldBe listOf(AccountProvider.TRAKT, AccountProvider.SIMKL)
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
        accountManager.setActiveProvider(AccountProvider.SIMKL)
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
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        presenter.dispatch(SwitchProviderClicked(AccountProvider.SIMKL))
        testScheduler.runCurrent()

        accountManager.setAccounts(
            listOf(ConnectedAccount(provider = AccountProvider.SIMKL, isConnected = true)),
        )
        testScheduler.runCurrent()

        accountManager.lastLogoutProvider shouldBe AccountProvider.TRAKT
        accountManager.getActiveProvider() shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should park at confirmation given unsaved changes remain`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        libraryRepository.setPendingFollowedShowsCount(3L)

        presenter.state.test {
            awaitItem()
            presenter.dispatch(SwitchProviderClicked(AccountProvider.SIMKL))

            var state = awaitItem()
            while (!state.showSwitchConfirmation) {
                state = awaitItem()
            }
            state.switchUnsavedCount shouldBe 3
            state.pendingSwitchProvider shouldBe AccountProvider.SIMKL
            cancelAndIgnoreRemainingEvents()
        }
        accountManager.lastLogoutProvider shouldBe null
    }

    @Test
    fun `should abort the switch given the confirmation is dismissed`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        libraryRepository.setPendingFollowedShowsCount(2L)

        presenter.state.test {
            awaitItem()
            presenter.dispatch(SwitchProviderClicked(AccountProvider.SIMKL))
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
        accountManager.getActiveProvider() shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should proceed with the switch given the user confirms discard`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        libraryRepository.setPendingFollowedShowsCount(1L)

        presenter.dispatch(SwitchProviderClicked(AccountProvider.SIMKL))
        testScheduler.runCurrent()

        presenter.dispatch(ConfirmSwitchDiscard)
        testScheduler.runCurrent()

        accountManager.setAccounts(
            listOf(ConnectedAccount(provider = AccountProvider.SIMKL, isConnected = true)),
        )
        testScheduler.runCurrent()

        accountManager.lastLogoutProvider shouldBe AccountProvider.TRAKT
        accountManager.getActiveProvider() shouldBe AccountProvider.SIMKL
    }
}
