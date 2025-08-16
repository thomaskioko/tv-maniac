package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.authenticatedAuthState
import com.thomaskioko.tvmaniac.settings.presenter.ChangeThemeClicked
import com.thomaskioko.tvmaniac.settings.presenter.DismissImageQualityDialog
import com.thomaskioko.tvmaniac.settings.presenter.DismissThemeClicked
import com.thomaskioko.tvmaniac.settings.presenter.DismissTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState.Companion.DEFAULT_STATE
import com.thomaskioko.tvmaniac.settings.presenter.ShowImageQualityDialog
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.TraktLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.TraktLogoutClicked
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

class SettingsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = SettingsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            datastoreRepository = datastoreRepository,
            traktAuthRepository = traktAuthRepository,
            launchWebView = {},
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

            datastoreRepository.setTheme(AppTheme.DARK_THEME)
            presenter.dispatch(ThemeSelected(AppTheme.DARK_THEME))

            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = true,
                    appTheme = AppTheme.DARK_THEME,
                )
            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = false,
                    appTheme = AppTheme.DARK_THEME,
                )
        }
    }

    @Test
    fun `should hide theme dialog when dismissed`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ChangeThemeClicked)

            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = true,
                )

            presenter.dispatch(DismissThemeClicked)

            awaitItem() shouldBe
                DEFAULT_STATE.copy(
                    showthemePopup = false,
                )
        }
    }

    @Test
    fun `should show and hide trakt dialog when toggled`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe
                DEFAULT_STATE.copy(
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
    fun `should hide trakt dialog when user logs in`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            presenter.dispatch(TraktLoginClicked)

            datastoreRepository.setAuthState(authenticatedAuthState)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)
        }
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun `should show error when trakt login fails`() = runTest {
        presenter.state.test {
            val errorMessage = "Something happened"

            awaitItem() shouldBe DEFAULT_STATE // Initial State

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)

            datastoreRepository.setAuthState(authenticatedAuthState)

            awaitItem() shouldBe DEFAULT_STATE.copy(errorMessage = errorMessage)
        }
    }

    @Test
    fun `should clear auth state when user logs out`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            presenter.dispatch(TraktLoginClicked)

            datastoreRepository.setAuthState(authenticatedAuthState)

            presenter.dispatch(TraktLogoutClicked)

            awaitItem() shouldBe DEFAULT_STATE
        }
    }

    @Test
    fun `should hide image quality dialog when dismissed`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ShowImageQualityDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showImageQualityDialog = true,
            )

            presenter.dispatch(DismissImageQualityDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showImageQualityDialog = false,
            )
        }
    }

    @Test
    fun `should update image quality when quality is selected`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ImageQualitySelected(ImageQuality.HIGH))
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                imageQuality = ImageQuality.HIGH,
            )

            presenter.dispatch(ImageQualitySelected(ImageQuality.LOW))
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                imageQuality = ImageQuality.LOW,
            )

            presenter.dispatch(ImageQualitySelected(ImageQuality.MEDIUM))
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                imageQuality = ImageQuality.MEDIUM,
            )
        }
    }

    @Test
    fun `should persist selected image quality when dialog is reopened`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ImageQualitySelected(ImageQuality.HIGH))
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                imageQuality = ImageQuality.HIGH,
            )

            presenter.dispatch(ShowImageQualityDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showImageQualityDialog = true,
                imageQuality = ImageQuality.HIGH,
            )

            presenter.dispatch(DismissImageQualityDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showImageQualityDialog = false,
                imageQuality = ImageQuality.HIGH,
            )
        }
    }
}
