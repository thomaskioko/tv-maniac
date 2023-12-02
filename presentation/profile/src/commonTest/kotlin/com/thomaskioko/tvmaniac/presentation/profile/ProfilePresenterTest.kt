package com.thomaskioko.tvmaniac.presentation.profile

import app.cash.turbine.test
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
class ProfilePresenterTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val profileRepository = FakeProfileRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var presenter: ProfilePresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
      /*  presenter = ProfilePresenter(
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
        presenter.state.test {
            awaitItem() shouldBe ProfileState()
        }
    }

    @Test
    fun given_ShowTraktDialog_andUserIsAuthenticated_expectedResultIsEmitted() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ProfileState() // Initial State

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe ProfileState()
                .copy(showTraktDialog = true)

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe ProfileState()
                .copy(showTraktDialog = false)

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Right(user))

            awaitItem() shouldBe ProfileState()
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
        presenter.state.test {
            val errorMessage = "Something happened"

            awaitItem() shouldBe ProfileState()

            presenter.dispatch(ShowTraktDialog)

            awaitItem() shouldBe ProfileState().copy(
                showTraktDialog = true,
            )

            presenter.dispatch(TraktLoginClicked)

            awaitItem() shouldBe ProfileState().copy(
                showTraktDialog = false,
            )

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(
                Either.Left(ServerError(errorMessage)),
            )

            awaitItem() shouldBe ProfileState()
                .copy(errorMessage = errorMessage)
        }
    }

    @Test
    fun given_TraktLogoutClicked_expectedResultIsEmitted() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ProfileState()

            traktAuthRepository.setAuthState(TraktAuthState.LOGGED_IN)
            datastoreRepository.setAuthState(authenticatedAuthState)
            profileRepository.setUserData(Either.Right(user))

            awaitItem() shouldBe ProfileState()
                .copy(
                    errorMessage = null,
                    userInfo = UserInfo(
                        slug = user.slug,
                        userName = user.user_name,
                        fullName = user.full_name,
                        userPicUrl = user.profile_picture,
                    ),
                )

            presenter.dispatch(TraktLogoutClicked)

            awaitItem() shouldBe ProfileState()
        }
    }
}
