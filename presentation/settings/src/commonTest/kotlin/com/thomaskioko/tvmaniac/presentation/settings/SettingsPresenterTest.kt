package com.thomaskioko.tvmaniac.presentation.settings

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.authenticatedAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class SettingsPresenterTest {

  private val lifecycle = LifecycleRegistry()
  private val testDispatcher = StandardTestDispatcher()
  private val datastoreRepository = FakeDatastoreRepository()
  private val traktAuthRepository = FakeTraktAuthRepository()

  private lateinit var presenter: SettingsPresenter

  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    presenter =
      SettingsPresenter(
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
  fun initial_state_emits_expected_result() = runTest {
    presenter.state.test { awaitItem() shouldBe SettingsState.DEFAULT_STATE }
  }

  @Test
  fun when_theme_is_updated_expected_result_is_emitted() = runTest {
    presenter.state.test {
      awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

      presenter.dispatch(ChangeThemeClicked)
      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showthemePopup = true,
        )

      datastoreRepository.setTheme(AppTheme.DARK_THEME)
      presenter.dispatch(ThemeSelected(AppTheme.DARK_THEME))

      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showthemePopup = true,
          appTheme = AppTheme.DARK_THEME,
        )
      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showthemePopup = false,
          appTheme = AppTheme.DARK_THEME,
        )
    }
  }

  @Test
  fun when_dialog_is_dismissed_expected_result_is_emitted() = runTest {
    presenter.state.test {
      awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

      presenter.dispatch(ChangeThemeClicked)

      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showthemePopup = true,
        )

      presenter.dispatch(DismissThemeClicked)

      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showthemePopup = false,
        )
    }
  }

  @Test
  fun when_ShowTraktDialog_is_clicked_expected_result_is_emitted() = runTest {
    presenter.state.test {
      awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

      presenter.dispatch(ShowTraktDialog)

      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showTraktDialog = true,
        )

      presenter.dispatch(DismissTraktDialog)

      awaitItem() shouldBe
        SettingsState.DEFAULT_STATE.copy(
          showTraktDialog = false,
        )
    }
  }

  @Test
  fun given_TraktLoginClicked_andUserIsAuthenticated_expectedResultIsEmitted() = runTest {
    presenter.state.test {
      awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

      presenter.dispatch(ShowTraktDialog)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(showTraktDialog = true)

      presenter.dispatch(TraktLoginClicked)

      datastoreRepository.setAuthState(authenticatedAuthState)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(showTraktDialog = false)
    }
  }

  @Ignore // "Fix once TraktAuthManager is implemented"
  @Test
  fun given_TraktLoginClicked_andErrorOccurs_expectedResultIsEmitted() = runTest {
    presenter.state.test {
      val errorMessage = "Something happened"

      awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

      presenter.dispatch(ShowTraktDialog)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(showTraktDialog = true)

      presenter.dispatch(TraktLoginClicked)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(showTraktDialog = false)

      datastoreRepository.setAuthState(authenticatedAuthState)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(errorMessage = errorMessage)
    }
  }

  @Test
  fun given_TraktLogoutClicked_expectedResultIsEmitted() = runTest {
    presenter.state.test {
      awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

      presenter.dispatch(ShowTraktDialog)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(showTraktDialog = true)

      presenter.dispatch(TraktLoginClicked)

      datastoreRepository.setAuthState(authenticatedAuthState)

      presenter.dispatch(TraktLogoutClicked)

      awaitItem() shouldBe SettingsState.DEFAULT_STATE
    }
  }
}
