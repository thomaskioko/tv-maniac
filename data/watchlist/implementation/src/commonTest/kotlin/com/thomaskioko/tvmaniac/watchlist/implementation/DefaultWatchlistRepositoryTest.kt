package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsDao
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.watchlist.implementation.fixtures.FakeTraktListRemoteDataSource
import com.thomaskioko.tvmaniac.watchlist.implementation.fixtures.FakeWatchlistDao
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

private val testInstant = Instant.DISTANT_PAST

@OptIn(ExperimentalCoroutinesApi::class)
@IgnoreIos
internal class DefaultWatchlistRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeFollowedShowsDao = FakeFollowedShowsDao()
    private val fakeWatchlistDao = FakeWatchlistDao()
    private val fakeTraktListDataSource = FakeTraktListRemoteDataSource()
    private val fakeAuthRepository = FakeTraktAuthRepository()
    private val fakeRequestManagerRepository = FakeRequestManagerRepository()
    private val fakeDatastoreRepository = FakeDatastoreRepository()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val transactionRunner = FakeTransactionRunner()
    private lateinit var fakeTraktActivityDao: FakeTraktActivityDao
    private lateinit var watchlistStore: WatchlistStore
    private lateinit var repository: DefaultWatchlistRepository

    @BeforeTest
    fun setup() {
        fakeTraktActivityDao = FakeTraktActivityDao(database)
        watchlistStore = WatchlistStore(
            traktListDataSource = fakeTraktListDataSource,
            followedShowsDao = fakeFollowedShowsDao,
            requestManagerRepository = fakeRequestManagerRepository,
            traktActivityDao = fakeTraktActivityDao,
            transactionRunner = transactionRunner,
            dispatchers = coroutineDispatcher,
        )
        repository = DefaultWatchlistRepository(
            watchlistDao = fakeWatchlistDao,
            followedShowsDao = fakeFollowedShowsDao,
            watchlistStore = watchlistStore,
            datastoreRepository = fakeDatastoreRepository,
            traktListDataSource = fakeTraktListDataSource,
            requestManagerRepository = fakeRequestManagerRepository,
            traktAuthRepository = fakeAuthRepository,
            transactionRunner = transactionRunner,
            traktActivityRepository = fakeTraktActivityRepository,
            logger = FakeLogger(),
        )
    }

    @Test
    fun `should skip sync given not logged in`() = runTest {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_OUT)

        repository.syncWatchlist()

        fakeTraktListDataSource.addShowsCallCount shouldBe 0
        fakeTraktListDataSource.removeShowsCallCount shouldBe 0
    }

    @Test
    fun `should process pending uploads during sync`() = runTest {
        setLoggedIn()
        val _ = fakeFollowedShowsDao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = fakeFollowedShowsDao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        repository.syncWatchlist()

        fakeTraktListDataSource.addShowsCallCount shouldBe 2
        fakeTraktListDataSource.lastAddedTraktIds shouldBe listOf(1L, 2L)

        val entries = fakeFollowedShowsDao.entries()
        entries.all { it.pendingAction == PendingAction.NOTHING } shouldBe true
    }

    @Test
    fun `should process pending deletes during sync`() = runTest {
        setLoggedIn()
        val _ = fakeFollowedShowsDao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
            ),
        )
        val _ = fakeFollowedShowsDao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
            ),
        )

        repository.syncWatchlist()

        fakeTraktListDataSource.removeShowsCallCount shouldBe 2
        fakeTraktListDataSource.lastRemovedTraktIds shouldBe listOf(1L, 2L)

        val entries = fakeFollowedShowsDao.entries()
        entries.size shouldBe 0
    }

    @Test
    fun `should return needs sync given request expired`() = runTest {
        fakeRequestManagerRepository.requestValid = false

        val needsSync = repository.needsSync()

        needsSync shouldBe true
    }

    @Test
    fun `should return no sync needed given request valid`() = runTest {
        fakeRequestManagerRepository.requestValid = true

        val needsSync = repository.needsSync()

        needsSync shouldBe false
    }

    private suspend fun setLoggedIn() {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_IN)
        fakeAuthRepository.setAuthState(
            AuthState(
                accessToken = "test_access_token",
                refreshToken = "test_refresh_token",
                isAuthorized = true,
            ),
        )
    }
}

private class FakeTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}
