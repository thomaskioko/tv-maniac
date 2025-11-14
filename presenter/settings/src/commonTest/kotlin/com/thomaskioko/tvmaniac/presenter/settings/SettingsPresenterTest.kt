package com.thomaskioko.tvmaniac.presenter.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.settings.presenter.ChangeThemeClicked
import com.thomaskioko.tvmaniac.settings.presenter.DefaultSettingsPresenter
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
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.SimpleAuthState
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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class SettingsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    private lateinit var presenter: SettingsPresenter

    private val testAuthState = SimpleAuthState(
        accessToken = "test_access_token",
        refreshToken = "test_refresh_token",
        isAuthorized = true,
        expiresAt = Clock.System.now() + 1.hours,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = DefaultSettingsPresenter(
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

            traktAuthRepository.setAuthState(testAuthState)
            traktAuthRepository.setRefreshAuthState(testAuthState)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)
        }
    }

    @Test
    fun `should show error when trakt login fails`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE // Initial State

            traktAuthRepository.setAuthError(AuthError.NetworkError)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                errorMessage = "No internet connection. Please check your network.",
            )
        }
    }

    @Test
    fun `should clear auth state when user logs out`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setAuthState(testAuthState)
            traktAuthRepository.setRefreshAuthState(testAuthState)

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)

            presenter.dispatch(TraktLogoutClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            expectNoEvents()
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

    @Test
    fun `should show loading state during authentication`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setIsAuthenticating(true)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(isLoading = true)

            traktAuthRepository.setIsAuthenticating(false)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(isLoading = false)
        }
    }

    @Test
    fun `should show OAuth cancelled error message`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setAuthError(AuthError.OAuthCancelled)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                errorMessage = "Authentication cancelled.",
            )
        }
    }

    @Test
    fun `should show OAuth failed error message with details`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setAuthError(AuthError.OAuthFailed("Invalid client"))
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                errorMessage = "Authentication failed: Invalid client",
            )
        }
    }

    @Test
    fun `should show token exchange failed error message`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setAuthError(AuthError.TokenExchangeFailed)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                errorMessage = "Failed to complete authentication.",
            )
        }
    }

    @Test
    fun `should show unknown error message`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setAuthError(AuthError.Unknown)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                errorMessage = "An error occurred. Please try again.",
            )
        }
    }

    @Test
    fun `should clear error when new authentication starts`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            traktAuthRepository.setAuthError(AuthError.NetworkError)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                errorMessage = "No internet connection. Please check your network.",
            )

            traktAuthRepository.setAuthError(null)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(errorMessage = null)
        }
    }

    @Test
    fun `should hide trakt dialog when error occurs`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            traktAuthRepository.setAuthError(AuthError.TokenExchangeFailed)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() shouldBe DEFAULT_STATE.copy(
                showTraktDialog = false,
                errorMessage = "Failed to complete authentication.",
            )
        }
    }

    @Test
    fun `should refresh token instead of launching OAuth when user has valid token`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            traktAuthRepository.setAuthState(testAuthState)
            traktAuthRepository.setRefreshAuthState(testAuthState)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)
        }
    }

    @Test
    fun `should launch OAuth when user has no token`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            traktAuthRepository.setAuthState(null)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)
        }
    }

    @Test
    fun `should launch OAuth when token refresh fails`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe DEFAULT_STATE

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = true)

            traktAuthRepository.setAuthState(testAuthState)
            traktAuthRepository.setRefreshAuthState(null)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe DEFAULT_STATE.copy(showTraktDialog = false)
        }
    }
}
