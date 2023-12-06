package com.thomaskioko.tvmaniac.presentation.settings

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.authenticatedAuthState
import com.thomaskioko.tvmaniac.trakt.profile.testing.FakeProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.testing.user
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.ServerError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsPresenterTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val profileRepository = FakeProfileRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: SettingsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        /*   screenModel = SettingsPresenter(
               datastoreRepository = datastoreRepository,
               profileRepository = profileRepository,
               traktAuthRepository = traktAuthRepository,
           )*/
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_emits_expected_result() = runTest {
        presenter.state shouldBe SettingsState.DEFAULT_STATE
    }

    @Test
    fun when_theme_is_updated_expected_result_is_emitted() = runTest {
        presenter.state shouldBe SettingsState.DEFAULT_STATE // Initial State

        presenter.dispatch(ChangeThemeClicked)
        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showthemePopup = true,
        )

        datastoreRepository.setTheme(AppTheme.DARK_THEME)
        presenter.dispatch(ThemeSelected(AppTheme.DARK_THEME))

        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showthemePopup = true,
            appTheme = AppTheme.DARK_THEME,
        )
        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showthemePopup = false,
            appTheme = AppTheme.DARK_THEME,
        )
    }

    @Test
    fun when_dialog_is_dismissed_expected_result_is_emitted() = runTest {
        presenter.state shouldBe SettingsState.DEFAULT_STATE // Initial State

        presenter.dispatch(ChangeThemeClicked)

        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showthemePopup = true,
        )

        presenter.dispatch(DismissThemeClicked)

        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showthemePopup = false,
        )
    }

    @Test
    fun when_ShowTraktDialog_is_clicked_expected_result_is_emitted() = runTest {
        presenter.state shouldBe SettingsState.DEFAULT_STATE // Initial State

        presenter.dispatch(ShowTraktDialog)

        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showTraktDialog = true,
        )

        presenter.dispatch(DismissTraktDialog)

        presenter.state shouldBe SettingsState.DEFAULT_STATE.copy(
            showTraktDialog = false,
        )
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun given_TraktLoginClicked_andUserIsAuthenticated_expectedResultIsEmitted() = runTest {
        presenter.state shouldBe SettingsState.DEFAULT_STATE // Initial State

        presenter.dispatch(ShowTraktDialog)

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(showTraktDialog = true)

        presenter.dispatch(TraktLoginClicked)

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(showTraktDialog = false)

        traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
        datastoreRepository.setAuthState(authenticatedAuthState)
        profileRepository.setUserData(Either.Right(user))

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(
                errorMessage = null,
                userInfo = UserInfo(
                    slug = user.slug,
                    userName = user.user_name,
                    fullName = user.full_name,
                    userPicUrl = user.profile_picture,
                ),
            )
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun given_TraktLoginClicked_andErrorOccurs_expectedResultIsEmitted() = runTest {
        val errorMessage = "Something happened"

        presenter.state shouldBe SettingsState.DEFAULT_STATE // Initial State

        presenter.dispatch(ShowTraktDialog)

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(showTraktDialog = true)

        presenter.dispatch(TraktLoginClicked)

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(showTraktDialog = false)

        traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
        datastoreRepository.setAuthState(authenticatedAuthState)
        profileRepository.setUserData(Either.Left(ServerError(errorMessage)))

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(errorMessage = errorMessage)
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun given_TraktLogoutClicked_expectedResultIsEmitted() = runTest {
        presenter.state shouldBe SettingsState.DEFAULT_STATE // Initial State

        presenter.dispatch(ShowTraktDialog)

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(showTraktDialog = true)

        presenter.dispatch(TraktLoginClicked)

        traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
        datastoreRepository.setAuthState(authenticatedAuthState)
        profileRepository.setUserData(Either.Right(user))

        presenter.state shouldBe SettingsState.DEFAULT_STATE
            .copy(
                errorMessage = null,
                userInfo = null,
            )

        presenter.dispatch(TraktLogoutClicked)

        traktAuthRepository.setAuthState(TraktAuthState.LOGGED_OUT)

        presenter.state shouldBe SettingsState.DEFAULT_STATE
    }
}
