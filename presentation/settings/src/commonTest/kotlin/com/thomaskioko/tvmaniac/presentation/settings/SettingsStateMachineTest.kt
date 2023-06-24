package com.thomaskioko.tvmaniac.presentation.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.authenticatedAuthState
import com.thomaskioko.tvmaniac.trakt.profile.testing.FakeProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.testing.user
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import kotlin.test.Test

class SettingsStateMachineTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val profileRepository = FakeProfileRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val stateMachine = SettingsStateMachine(
        datastoreRepository = datastoreRepository,
        profileRepository = profileRepository,
        traktAuthRepository = traktAuthRepository,
    )

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe Default.EMPTY
        }
    }

    @Test
    fun when_theme_is_updated_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe Default.EMPTY // Initial State

            stateMachine.dispatch(ChangeThemeClicked)
            awaitItem() shouldBe Default.EMPTY.copy(
                showPopup = true,
            )

            datastoreRepository.setTheme(Theme.DARK)
            stateMachine.dispatch(ThemeSelected(Theme.DARK))

            awaitItem() shouldBe Default.EMPTY.copy(
                showPopup = true,
                theme = Theme.DARK,
            )
            awaitItem() shouldBe Default.EMPTY.copy(
                showPopup = false,
                theme = Theme.DARK,
            )
        }
    }

    @Test
    fun when_dialog_is_dismissed_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe Default.EMPTY // Initial State

            stateMachine.dispatch(ChangeThemeClicked)

            awaitItem() shouldBe Default.EMPTY.copy(
                showPopup = true,
            )

            stateMachine.dispatch(DimissThemeClicked)

            awaitItem() shouldBe Default.EMPTY.copy(
                showPopup = false,
            )
        }
    }

    @Test
    fun when_ShowTraktDialog_is_clicked_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe Default.EMPTY // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe Default.EMPTY.copy(
                showTraktDialog = true,
            )

            stateMachine.dispatch(DismissTraktDialog)

            awaitItem() shouldBe Default.EMPTY.copy(
                showTraktDialog = false,
            )
        }
    }

    @Test
    fun given_TraktLoginClicked_andUserIsAuthenticated_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe Default.EMPTY // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe Default.EMPTY
                .copy(
                    showTraktDialog = true,
                )

            stateMachine.dispatch(TraktLoginClicked)

            awaitItem() shouldBe Default.EMPTY
                .copy(
                    showTraktDialog = false,
                )

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(
                StoreReadResponse.Data(
                    value = user,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            awaitItem() shouldBe LoggedInContent.DEFAULT_STATE
            awaitItem() shouldBe LoggedInContent.DEFAULT_STATE
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

    @Test
    fun given_TraktLoginClicked_andErrorOccurs_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something happened"

            awaitItem() shouldBe Default.EMPTY // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe Default.EMPTY.copy(
                showTraktDialog = true,
            )

            stateMachine.dispatch(TraktLoginClicked)

            awaitItem() shouldBe Default.EMPTY.copy(
                showTraktDialog = false,
            )

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(
                StoreReadResponse.Error.Exception(
                    error = Throwable(errorMessage),
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            awaitItem() shouldBe LoggedInContent.DEFAULT_STATE
            awaitItem() shouldBe LoggedInContent.DEFAULT_STATE
                .copy(
                    errorMessage = errorMessage,
                )
        }
    }

    @Test
    fun given_TraktLogoutClicked_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe Default.EMPTY // Initial State

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(
                StoreReadResponse.Data(
                    value = user,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            awaitItem() shouldBe LoggedInContent.DEFAULT_STATE
            awaitItem() shouldBe LoggedInContent.DEFAULT_STATE
                .copy(
                    errorMessage = null,
                    userInfo = UserInfo(
                        slug = user.slug,
                        userName = user.user_name,
                        fullName = user.full_name,
                        userPicUrl = user.profile_picture,
                    ),
                )

            stateMachine.dispatch(TraktLogoutClicked)

            awaitItem() shouldBe Default.EMPTY
        }
    }
}
