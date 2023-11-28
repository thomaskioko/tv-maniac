package com.thomaskioko.tvmaniac.presentation.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.api.Theme
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenModelTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val profileRepository = FakeProfileRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var stateMachine: SettingsScreenModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        stateMachine = SettingsScreenModel(
            datastoreRepository = datastoreRepository,
            profileRepository = profileRepository,
            traktAuthRepository = traktAuthRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsState.DEFAULT_STATE
        }
    }

    @Test
    fun when_theme_is_updated_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ChangeThemeClicked)
            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showthemePopup = true,
            )

            datastoreRepository.setTheme(Theme.DARK)
            stateMachine.dispatch(ThemeSelected(Theme.DARK))

            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showthemePopup = true,
                theme = Theme.DARK,
            )
            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showthemePopup = false,
                theme = Theme.DARK,
            )
        }
    }

    @Test
    fun when_dialog_is_dismissed_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ChangeThemeClicked)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showthemePopup = true,
            )

            stateMachine.dispatch(DismissThemeClicked)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showthemePopup = false,
            )
        }
    }

    @Test
    fun when_ShowTraktDialog_is_clicked_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showTraktDialog = true,
            )

            stateMachine.dispatch(DismissTraktDialog)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE.copy(
                showTraktDialog = false,
            )
        }
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun given_TraktLoginClicked_andUserIsAuthenticated_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(showTraktDialog = true)

            stateMachine.dispatch(TraktLoginClicked)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(showTraktDialog = false)

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Right(user))

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
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
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun given_TraktLoginClicked_andErrorOccurs_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something happened"

            awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(showTraktDialog = true)

            stateMachine.dispatch(TraktLoginClicked)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(showTraktDialog = false)

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Left(ServerError(errorMessage)))

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(errorMessage = errorMessage)
        }
    }

    @Ignore // "Fix once TraktAuthManager is implemented"
    @Test
    fun given_TraktLogoutClicked_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsState.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(showTraktDialog = true)

            stateMachine.dispatch(TraktLoginClicked)

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Right(user))

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
                .copy(
                    errorMessage = null,
                    userInfo = null,
                )

            stateMachine.dispatch(TraktLogoutClicked)

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_OUT)

            awaitItem() shouldBe SettingsState.DEFAULT_STATE
        }
    }
}
