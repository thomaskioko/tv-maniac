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
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState.Companion.DEFAULT_STATE
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.toAppTheme
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
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
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val userRepository = FakeUserRepository()

    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = DefaultSettingsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            datastoreRepository = datastoreRepository,
            traktAuthRepository = traktAuthRepository,
            logger = FakeLogger(),
            logoutInteractor = LogoutInteractor(
                traktAuthRepository = traktAuthRepository,
                userRepository = userRepository,
            ),
            backClicked = {},
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit default state when initialized`() = runTest {
        presenter.state.test { awaitItem() shouldBe DEFAULT_STATE }
    }

    @Test
    fun `should update theme when theme is selected`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ChangeThemeClicked)
            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = true,
                )

            datastoreRepository.setTheme(ThemeModel.DARK.toAppTheme())
            presenter.dispatch(ThemeSelected(ThemeModel.DARK))

            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = true,
                    theme = ThemeModel.DARK,
                )
            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = false,
                    theme = ThemeModel.DARK,
                )
        }
    }

    @Test
    fun `should hide theme dialog when dismissed`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ChangeThemeClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showthemePopup = true,
            )

            presenter.dispatch(DismissThemeClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showthemePopup = false,
            )
        }
    }

    @Test
    fun `should show and hide trakt dialog when toggled`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showTraktDialog = true,
            )

            presenter.dispatch(DismissTraktDialog)

            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showTraktDialog = false,
                )
        }
    }

    @Test
    fun `should update image quality when quality is selected`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ImageQualitySelected(ImageQuality.HIGH))

            awaitItem() shouldBe DEFAULT_STATE.copy(
                imageQuality = ImageQuality.HIGH,
            )

            presenter.dispatch(ImageQualitySelected(ImageQuality.LOW))

            awaitItem() shouldBe DEFAULT_STATE.copy(
                imageQuality = ImageQuality.LOW,
            )
        }
    }
}
