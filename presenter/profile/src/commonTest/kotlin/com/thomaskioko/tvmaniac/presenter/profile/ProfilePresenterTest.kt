package com.thomaskioko.tvmaniac.presenter.profile

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.data.user.api.model.UserWatchTime
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.data.user.testing.createTestProfile
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.profile.presenter.DefaultProfilePresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
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

internal class ProfilePresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val userRepository = FakeUserRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val traktAuthManager = FakeTraktAuthManager()
    private val logger = FakeLogger()
    private val testProfile = createTestProfile(
        stats = UserProfileStats(
            showsWatched = 10,
            episodesWatched = 100,
            userWatchTime = UserWatchTime(
                years = 0,
                days = 4,
                hours = 4,
                minutes = 0,
            ),
        ),
    )
    private val testDispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )

    private val observeUserProfileInteractor = ObserveUserProfileInteractor(
        userRepository = userRepository,
        traktAuthRepository = traktAuthRepository,
    )

    private val updateUserProfileData = UpdateUserProfileData(
        userRepository = userRepository,
        dispatchers = testDispatchers,
    )

    private lateinit var presenter: ProfilePresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        presenter = createPresenter()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit default state when initialized`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE
        }
    }

    @Test
    fun `should load profile when user is authenticated`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        userRepository.setUserProfile(testProfile)

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false
        }
    }

    @Test
    fun `should update state when auth state changes to logged in`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            userRepository.setUserProfile(testProfile)
            traktAuthRepository.setState(TraktAuthState.LOGGED_IN)

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false
        }
    }

    @Test
    fun `should update profile when user data changes`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        userRepository.setUserProfile(testProfile)

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false

            val updatedProfile = testProfile.copy(
                username = "updated-user",
                stats = testProfile.stats.copy(showsWatched = 20),
            )
            userRepository.setUserProfile(updatedProfile)

            val updatedState = awaitItem()
            updatedState.userProfile?.username shouldBe "updated-user"
            updatedState.userProfile?.stats?.showsWatched shouldBe 20
        }
    }

    @Test
    fun `should load profile after authentication`() = runTest {
        userRepository.setUserProfile(null)

        val testPresenter = createPresenter()

        testPresenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
            userRepository.setUserProfile(testProfile)

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false
            loadedState.isRefreshing shouldBe false
        }
    }

    private fun createExpectedProfileInfo(profile: UserProfile): ProfileInfo {
        return ProfileInfo(
            slug = profile.slug,
            username = profile.username,
            fullName = profile.fullName,
            avatarUrl = profile.avatarUrl,
            backgroundUrl = profile.backgroundUrl,
            stats = ProfileStats(
                showsWatched = profile.stats.showsWatched.toInt(),
                episodesWatched = profile.stats.episodesWatched.toInt(),
                years = 0,
                months = 0,
                days = 4,
                hours = 4,
                minutes = 0,
            ),
        )
    }

    private fun createPresenter(): ProfilePresenter {
        return DefaultProfilePresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            onSettings = { },
            traktAuthManager = traktAuthManager,
            updateUserProfileData = updateUserProfileData,
            logger = logger,
            observeUserProfileInteractor = observeUserProfileInteractor,
        )
    }
}
