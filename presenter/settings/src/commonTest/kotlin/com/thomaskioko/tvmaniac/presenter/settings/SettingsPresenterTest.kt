package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ToggleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.settings.presenter.ChangeThemeClicked
import com.thomaskioko.tvmaniac.settings.presenter.DefaultSettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.DismissThemeClicked
import com.thomaskioko.tvmaniac.settings.presenter.DismissTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.toAppTheme
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.testing.FakeApplicationInfo
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

@IgnoreIos
class SettingsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val userRepository = FakeUserRepository()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val fakeLogger = FakeLogger()
    private val fakeCrashReporter = FakeCrashReporter()
    private val fakeScheduler = object : BackgroundTaskScheduler {
        override fun schedulePeriodic(request: PeriodicTaskRequest) = Unit
        override fun scheduleAndExecute(request: PeriodicTaskRequest) = Unit
        override fun cancel(id: String) = Unit
        override fun cancelAll() = Unit
    }

    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = DefaultSettingsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            appInfo = FakeApplicationInfo.DEFAULT,
            crashReporter = fakeCrashReporter,
            datastoreRepository = datastoreRepository,
            traktAuthRepository = traktAuthRepository,
            logger = fakeLogger,
            logoutInteractor = LogoutInteractor(
                traktAuthRepository = traktAuthRepository,
                userRepository = userRepository,
                datastoreRepository = datastoreRepository,
                traktActivityRepository = fakeTraktActivityRepository,
            ),
            observeSettingsPreferencesInteractor = ObserveSettingsPreferencesInteractor(
                datastoreRepository = datastoreRepository,
                dateTimeProvider = dateTimeProvider,
            ),
            toggleEpisodeNotificationsInteractor = ToggleEpisodeNotificationsInteractor(
                datastoreRepository = datastoreRepository,
                scheduler = fakeScheduler,
                traktAuthRepository = traktAuthRepository,
            ),
            backClicked = {},
            onNavigateToDebugMenu = {},
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

            datastoreRepository.setTheme(ThemeModel.DARK.toAppTheme())

            presenter.dispatch(ChangeThemeClicked)
            awaitItem().showthemePopup shouldBe true

            presenter.dispatch(ThemeSelected(ThemeModel.DARK))

            val updatedState = awaitItem()
            updatedState.showthemePopup shouldBe false
            updatedState.theme shouldBe ThemeModel.DARK
        }
    }

    @Test
    fun `should hide theme dialog when dismissed`() = runTest {
        presenter.state.test {
            awaitItem()

            presenter.dispatch(ChangeThemeClicked)
            awaitItem().showthemePopup shouldBe true

            presenter.dispatch(DismissThemeClicked)
            awaitItem().showthemePopup shouldBe false
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
}
