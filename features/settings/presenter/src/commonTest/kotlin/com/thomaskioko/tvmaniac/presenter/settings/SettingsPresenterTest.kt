package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAuthManager
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.DismissTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.TraktLoginClicked
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.util.testing.FakeAppMetadata
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val fakeActivitySyncRepository = FakeActivitySyncRepository()
    private val fakeRequestManagerRepository = FakeRequestManagerRepository()
    private val fakeLogger = FakeLogger()
    private val localizer = FakeLocalizer()
    private val authManager = FakeAuthManager()
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
            authManagers = setOf(authManager),
            logoutInteractor = LogoutInteractor(
                accountManager = accountManager,
                userRepository = userRepository,
                datastoreRepository = datastoreRepository,
                traktActivityRepository = fakeTraktActivityRepository,
                syncRepository = fakeActivitySyncRepository,
                requestManagerRepository = fakeRequestManagerRepository,
            ),
            observeSettingsPreferencesInteractor = ObserveSettingsPreferencesInteractor(
                datastoreRepository = datastoreRepository,
                dateTimeProvider = dateTimeProvider,
            ),
            toggleEpisodeNotificationsInteractor = ToggleEpisodeNotificationsInteractor(
                datastoreRepository = datastoreRepository,
            ),
            navigator = NoOpNavigator(),
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

            presenter.dispatch(ShowTraktDialog)
            awaitItem().showTraktDialog shouldBe true

            presenter.dispatch(DismissTraktDialog)
            awaitItem().showTraktDialog shouldBe false
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

        presenter.dispatch(TraktLoginClicked)
        testScheduler.advanceUntilIdle()

        launched shouldBe true
    }
}
