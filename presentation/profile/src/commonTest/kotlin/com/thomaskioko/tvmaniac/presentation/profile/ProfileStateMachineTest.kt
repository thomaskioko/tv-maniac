package com.thomaskioko.tvmaniac.presentation.profile

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.ServerError
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.authenticatedAuthState
import com.thomaskioko.tvmaniac.trakt.profile.testing.FakeProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.testing.user
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ProfileStateMachineTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val profileRepository = FakeProfileRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    private val stateMachine = ProfileStateMachine(
        datastoreRepository = datastoreRepository,
        profileRepository = profileRepository,
        traktAuthRepository = traktAuthRepository,
    )

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe LoggedOutContent()
        }
    }

    @Test
    fun given_ShowTraktDialog_andUserIsAuthenticated_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE // Initial State

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE
                .copy(
                    showTraktDialog = true,
                )

            stateMachine.dispatch(TraktLoginClicked)

            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE
                .copy(
                    showTraktDialog = false,
                )

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Right(user))

            awaitItem() shouldBe LoggedInContent()
            awaitItem() shouldBe LoggedInContent()
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

            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE

            stateMachine.dispatch(ShowTraktDialog)

            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE.copy(
                showTraktDialog = true,
            )

            stateMachine.dispatch(TraktLoginClicked)

            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE.copy(
                showTraktDialog = false,
            )

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(
                Either.Left(ServerError(errorMessage)),
            )

            awaitItem() shouldBe LoggedInContent()
            awaitItem() shouldBe LoggedInContent()
                .copy(
                    errorMessage = errorMessage,
                )
        }
    }

    @Test
    fun given_TraktLogoutClicked_expectedResultIsEmitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Right(user))

            awaitItem() shouldBe LoggedInContent()
            awaitItem() shouldBe LoggedInContent()
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

            awaitItem() shouldBe LoggedOutContent.DEFAULT_STATE
        }
    }
}
