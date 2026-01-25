package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
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

    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = DefaultSettingsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            datastoreRepository = datastoreRepository,
            dateTimeProvider = dateTimeProvider,
            traktAuthRepository = traktAuthRepository,
            logger = FakeLogger(),
            logoutInteractor = LogoutInteractor(
                traktAuthRepository = traktAuthRepository,
                userRepository = userRepository,
                datastoreRepository = datastoreRepository,
                traktActivityRepository = fakeTraktActivityRepository,
            ),
            appInfo = FakeApplicationInfo.DEFAULT,
            backClicked = {},
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

            presenter.dispatch(ChangeThemeClicked)
            awaitItem().showthemePopup shouldBe true

            datastoreRepository.setTheme(ThemeModel.DARK.toAppTheme())
            presenter.dispatch(ThemeSelected(ThemeModel.DARK))

            val updatedState1 = awaitItem()
            updatedState1.showthemePopup shouldBe true
            updatedState1.theme shouldBe ThemeModel.DARK

            val updatedState2 = awaitItem()
            updatedState2.showthemePopup shouldBe false
            updatedState2.theme shouldBe ThemeModel.DARK
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
