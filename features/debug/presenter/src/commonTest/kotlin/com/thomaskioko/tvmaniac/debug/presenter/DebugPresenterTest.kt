package com.thomaskioko.tvmaniac.debug.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor
import com.thomaskioko.tvmaniac.domain.upnext.RefreshUpNextInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.LabelDebugNeverRefreshed
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DebugPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val logger = FakeLogger()
    private val localizer = FakeLocalizer()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return null token status given logged out`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)

        val presenter = createPresenter()

        advanceUntilIdle()

        presenter.state.test {
            val state = expectMostRecentItem()
            state.tokenStatusSubtitle shouldBe null
        }
    }

    @Test
    fun `should return valid token status given logged in with authorized auth state`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        traktAuthRepository.setAuthState(
            AuthState(
                accessToken = "test-token",
                refreshToken = "test-refresh",
                isAuthorized = true,
            ),
        )
        datastoreRepository.setLastTokenRefreshTimestamp(1_700_000_000_000L)
        dateTimeProvider.setEpochToDisplayDateTimeResult("Mar 22, 2026 at 10:00")

        val presenter = createPresenter()

        presenter.state.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()

            val expectedDate = "Mar 22, 2026 at 10:00"
            state.tokenStatusSubtitle shouldBe localizer.getString(
                StringResourceKey.LabelDebugTokenRefreshValid,
                expectedDate,
            )
        }
    }

    @Test
    fun `should return expired token status given logged in with unauthorized auth state`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        traktAuthRepository.setAuthState(
            AuthState(
                accessToken = "test-token",
                refreshToken = "test-refresh",
                isAuthorized = false,
            ),
        )
        datastoreRepository.setLastTokenRefreshTimestamp(1_700_000_000_000L)
        dateTimeProvider.setEpochToDisplayDateTimeResult("Mar 22, 2026 at 10:00")

        val presenter = createPresenter()

        presenter.state.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()

            val expectedDate = "Mar 22, 2026 at 10:00"
            state.tokenStatusSubtitle shouldBe localizer.getString(
                StringResourceKey.LabelDebugTokenRefreshExpired,
                expectedDate,
            )
        }
    }

    @Test
    fun `should return never refreshed status given logged in with no refresh timestamp`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        traktAuthRepository.setAuthState(
            AuthState(
                accessToken = "test-token",
                refreshToken = "test-refresh",
                isAuthorized = true,
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()

            state.tokenStatusSubtitle shouldBe localizer.getString(LabelDebugNeverRefreshed)
        }
    }

    @Test
    fun `should return logged in given auth state is logged in`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)

        val presenter = createPresenter()

        presenter.state.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()

            state.isLoggedIn shouldBe true
        }
    }

    private fun createPresenter(): DebugPresenter {
        val dispatchers = AppCoroutineDispatchers(
            main = testDispatcher,
            io = testDispatcher,
            computation = testDispatcher,
            databaseWrite = testDispatcher,
            databaseRead = testDispatcher,
        )

        return DebugPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigator = object : DebugNavigator {
                override fun goBack() {}
            },
            datastoreRepository = datastoreRepository,
            scheduleDebugEpisodeNotificationInteractor = ScheduleDebugEpisodeNotificationInteractor(
                datastoreRepository = datastoreRepository,
                episodeRepository = FakeEpisodeRepository(),
                notificationManager = FakeNotificationManager(),
                localizer = localizer,
                dateTimeProvider = dateTimeProvider,
                logger = logger,
                dispatchers = dispatchers,
            ),
            syncLibraryInteractor = SyncLibraryInteractor(
                libraryRepository = FakeLibraryRepository(),
                followedShowsRepository = FakeFollowedShowsRepository(),
                showDetailsRepository = FakeShowDetailsRepository(),
                watchProviderRepository = FakeWatchProviderRepository(),
                traktActivityRepository = FakeTraktActivityRepository(),
                datastoreRepository = datastoreRepository,
                dateTimeProvider = dateTimeProvider,
                dispatchers = dispatchers,
                logger = logger,
            ),
            refreshUpNextInteractor = RefreshUpNextInteractor(
                upNextRepository = FakeUpNextRepository(),
                datastoreRepository = datastoreRepository,
                dateTimeProvider = dateTimeProvider,
            ),
            dateTimeProvider = dateTimeProvider,
            localizer = localizer,
            errorToStringMapper = { it.message ?: "Test error" },
            logger = logger,
            traktAuthRepository = traktAuthRepository,
        )
    }
}
