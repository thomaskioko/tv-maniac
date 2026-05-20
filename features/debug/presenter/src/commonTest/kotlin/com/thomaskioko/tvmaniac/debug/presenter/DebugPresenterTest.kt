package com.thomaskioko.tvmaniac.debug.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.LabelDebugNeverRefreshed
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

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
    fun `should omit token status item given logged out`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)

        val presenter = createPresenter()

        advanceUntilIdle()

        presenter.state.test {
            val state = expectMostRecentItem()
            state.items.none { it.id == "token-status" } shouldBe true
        }
    }

    @Test
    fun `should expose expires-in subtitle on token status item given logged in with future expiry`() = runTest {
        val now = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        dateTimeProvider.setCurrentTime(now)
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        traktAuthRepository.setAuthState(
            AuthState(
                accessToken = "test-token",
                refreshToken = "test-refresh",
                isAuthorized = true,
                expiresAt = now + 3.hours,
            ),
        )
        datastoreRepository.setLastTokenRefreshTimestamp(1_700_000_000_000L)
        dateTimeProvider.setEpochToDisplayDateTimeResult("Mar 22, 2026 at 10:00")

        val presenter = createPresenter()

        presenter.state.test {
            advanceUntilIdle()
            val tokenItem = expectMostRecentItem().items.first { it.id == "token-status" }

            tokenItem.subtitle shouldBe localizer.getString(
                StringResourceKey.LabelDebugTokenExpiresIn,
                "Mar 22, 2026 at 10:00",
                "3h 0m",
            )
        }
    }

    @Test
    fun `should expose expired subtitle on token status item given logged in with unauthorized auth state`() = runTest {
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
            val tokenItem = expectMostRecentItem().items.first { it.id == "token-status" }

            tokenItem.subtitle shouldBe localizer.getString(
                StringResourceKey.LabelDebugTokenExpired,
                "Mar 22, 2026 at 10:00",
            )
        }
    }

    @Test
    fun `should expose expired subtitle on token status item given logged in with past expiry`() = runTest {
        val now = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        dateTimeProvider.setCurrentTime(now)
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        traktAuthRepository.setAuthState(
            AuthState(
                accessToken = "test-token",
                refreshToken = "test-refresh",
                isAuthorized = true,
                expiresAt = now - 1.hours,
            ),
        )
        datastoreRepository.setLastTokenRefreshTimestamp(1_700_000_000_000L)
        dateTimeProvider.setEpochToDisplayDateTimeResult("Mar 22, 2026 at 10:00")

        val presenter = createPresenter()

        presenter.state.test {
            advanceUntilIdle()
            val tokenItem = expectMostRecentItem().items.first { it.id == "token-status" }

            tokenItem.subtitle shouldBe localizer.getString(
                StringResourceKey.LabelDebugTokenExpired,
                "Mar 22, 2026 at 10:00",
            )
        }
    }

    @Test
    fun `should expose never refreshed subtitle on token status item given logged in with no refresh timestamp`() = runTest {
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
            val tokenItem = expectMostRecentItem().items.first { it.id == "token-status" }

            tokenItem.subtitle shouldBe localizer.getString(LabelDebugNeverRefreshed)
        }
    }

    @Test
    fun `should expose default items including test crash row`() = runTest {
        val presenter = createPresenter()

        advanceUntilIdle()

        presenter.state.test {
            val state = expectMostRecentItem()
            val ids = state.items.map { it.id }
            ids shouldBe listOf(
                "notifications",
                "delayed-notification",
                "library-sync",
                "upnext-sync",
                "feature-flags",
                "test-crash",
            )
            state.items.first { it.id == "test-crash" }.role shouldBe DebugItemRole.Destructive
            state.title shouldBe localizer.getString(StringResourceKey.LabelDebugMenuTitle)
        }
    }

    @Test
    fun `should emit login required message given TriggerLibrarySync while logged out`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)

        val presenter = createPresenter()
        advanceUntilIdle()

        presenter.dispatch(TriggerLibrarySync)
        advanceUntilIdle()

        presenter.state.test {
            val message = expectMostRecentItem().message
            message shouldNotBe null
            message?.message shouldBe localizer.getString(StringResourceKey.LabelDebugSyncLoginRequired)
        }
    }

    @Test
    fun `should emit login required message given TriggerUpNextSync while logged out`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)

        val presenter = createPresenter()
        advanceUntilIdle()

        presenter.dispatch(TriggerUpNextSync)
        advanceUntilIdle()

        presenter.state.test {
            val message = expectMostRecentItem().message
            message shouldNotBe null
            message?.message shouldBe localizer.getString(StringResourceKey.LabelDebugSyncLoginRequired)
        }
    }

    @Test
    fun `should throw given TriggerTestCrash`() = runTest {
        val presenter = createPresenter()
        advanceUntilIdle()

        shouldThrow<RuntimeException> {
            presenter.dispatch(TriggerTestCrash)
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
            navigator = NoOpNavigator(),
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
                syncActivityInteractor = SyncActivityInteractor(
                    traktActivityRepository = FakeTraktActivityRepository(),
                    dispatchers = dispatchers,
                ),
                syncShowMetadataInteractor = SyncShowMetadataInteractor(
                    showDetailsRepository = FakeShowDetailsRepository(),
                    seasonDetailsRepository = FakeSeasonDetailsRepository(),
                    watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
                    watchProviderRepository = FakeWatchProviderRepository(),
                    dispatchers = dispatchers,
                ),
                datastoreRepository = datastoreRepository,
                dateTimeProvider = dateTimeProvider,
                dispatchers = dispatchers,
                syncObserver = FakeSyncObserver(),
                logger = logger,
            ),
            syncContinueWatchingInteractor = SyncContinueWatchingInteractor(
                syncActivityInteractor = SyncActivityInteractor(
                    traktActivityRepository = FakeTraktActivityRepository(),
                    dispatchers = dispatchers,
                ),
                continueWatchingRepository = FakeContinueWatchingRepository(),
                continueWatchingDao = FakeContinueWatchingDao(),
                syncShowMetadataInteractor = SyncShowMetadataInteractor(
                    showDetailsRepository = FakeShowDetailsRepository(),
                    seasonDetailsRepository = FakeSeasonDetailsRepository(),
                    watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
                    watchProviderRepository = FakeWatchProviderRepository(),
                    dispatchers = dispatchers,
                ),
                syncObserver = FakeSyncObserver(),
                dispatchers = dispatchers,
                logger = logger,
            ),
            dateTimeProvider = dateTimeProvider,
            localizer = localizer,
            errorToStringMapper = { it.message ?: "Test error" },
            logger = logger,
            traktAuthRepository = traktAuthRepository,
        )
    }
}
