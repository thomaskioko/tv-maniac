package com.thomaskioko.tvmaniac.followedshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.implementation.fixtures.FakeFollowedShowsDataSource
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

private val testInstant = Instant.DISTANT_PAST

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultFollowedShowsRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: DefaultFollowedShowsDao
    private lateinit var repository: DefaultFollowedShowsRepository
    private val fakeDataSource: FakeFollowedShowsDataSource = FakeFollowedShowsDataSource()
    private val fakeAuthRepository: FakeTraktAuthRepository = FakeTraktAuthRepository()
    private val fakeRequestManagerRepository: FakeRequestManagerRepository =
        FakeRequestManagerRepository()
    private val fakeDateTimeProvider: FakeDateTimeProvider = FakeDateTimeProvider()
    private val lastRequestStore: FollowedShowsLastRequestStore = FollowedShowsLastRequestStore(fakeRequestManagerRepository)
    private lateinit var transactionRunner: DatabaseTransactionRunner

    @BeforeTest
    fun setup() {
        dao = DefaultFollowedShowsDao(database, coroutineDispatcher)
        transactionRunner = DbTransactionRunner(database)
        repository = DefaultFollowedShowsRepository(
            followedShowsDao = dao,
            dataSource = fakeDataSource,
            lastRequestStore = lastRequestStore,
            traktAuthRepository = fakeAuthRepository,
            transactionRunner = transactionRunner,
            dateTimeProvider = fakeDateTimeProvider,
            dispatchers = coroutineDispatcher,
            logger = FakeLogger(),
        )
        insertTestShows()
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should add followed show with pending upload`() = runTest {
        repository.addFollowedShow(1L)

        val entries = dao.entries()

        entries.size shouldBe 1
        entries.first().traktId shouldBe 1L
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should re-add show marked for deletion`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
            ),
        )

        repository.addFollowedShow(1L)

        val entry = dao.entryWithTraktId(1L)
        entry?.pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should not add show already in watchlist`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        repository.addFollowedShow(1L)

        val entries = dao.entries()
        entries.size shouldBe 1
        entries.first().pendingAction shouldBe PendingAction.NOTHING
    }

    @Test
    fun `should mark show for deletion given trakt id exists`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        repository.removeFollowedShow(1L)

        val entry = dao.entryWithTraktId(1L)
        entry?.pendingAction shouldBe PendingAction.DELETE
    }

    @Test
    fun `should delete local entry given no trakt id`() = runTest {
        setLoggedIn()
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        repository.removeFollowedShow(1L)

        val entry = dao.entryWithTraktId(1L)
        entry shouldBe null
    }

    @Test
    fun `should observe followed shows`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

            val entries = repository.getFollowedShows()
            entries.size shouldBe 2
            entries.map { it.traktId }.toSet() shouldBe setOf(1L, 2L)
    }

    @Test
    fun `should skip sync given not logged in`() = runTest {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_OUT)

        repository.syncFollowedShows()

        fakeDataSource.addShowsCallCount shouldBe 0
        fakeDataSource.removeShowsCallCount shouldBe 0
    }

    @Test
    fun `should process pending uploads during sync`() = runTest {
        setLoggedIn()
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        repository.syncFollowedShows()

        fakeDataSource.addShowsCallCount shouldBe 1
        fakeDataSource.lastAddedTraktIds shouldBe listOf(1L, 2L)

        val entries = dao.entries()
        entries.all { it.pendingAction == PendingAction.NOTHING } shouldBe true
    }

    @Test
    fun `should process pending deletes during sync`() = runTest {
        setLoggedIn()
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
            ),
        )

        repository.syncFollowedShows()

        fakeDataSource.removeShowsCallCount shouldBe 1
        fakeDataSource.lastRemovedTraktIds shouldBe listOf(1L, 2L)

        val entries = dao.entries()
        entries.size shouldBe 0
    }

    @Test
    fun `should pull remote watchlist during sync`() = runTest {
        setLoggedIn()
        fakeRequestManagerRepository.requestValid = false
        fakeDataSource.followedShows = listOf(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        repository.syncFollowedShows()

        val entries = dao.entries()
        entries.size shouldBe 2
        entries.map { it.traktId }.toSet() shouldBe setOf(1L, 2L)

        fakeRequestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should sync remote additions and deletions`() = runTest {
        setLoggedIn()
        fakeRequestManagerRepository.requestValid = false
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        fakeDataSource.followedShows = listOf(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
            FollowedShowEntry(
                traktId = 3L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        repository.syncFollowedShows()

        val entries = dao.entries()
        entries.size shouldBe 2
        entries.map { it.traktId }.toSet() shouldBe setOf(2L, 3L)
    }

    @Test
    fun `should preserve pending upload entries after sync processes them`() = runTest {
        setLoggedIn()
        fakeRequestManagerRepository.requestValid = false
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                traktId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        fakeDataSource.followedShows = listOf(
            FollowedShowEntry(
                traktId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
            FollowedShowEntry(
                traktId = 3L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        repository.syncFollowedShows()

        val entries = dao.entries()
        entries.any { it.traktId == 1L } shouldBe true
        entries.find { it.traktId == 1L }?.pendingAction shouldBe PendingAction.NOTHING
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

    private fun insertTestShows() {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(1),
            tmdb_id = Id<TmdbId>(1),
            name = "Test Show 1",
            overview = "Test overview 1",
            language = "en",
            year = "2023-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama", "Action"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/test1.jpg",
            backdrop_path = "/backdrop1.jpg",
        )

        val _ = database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(2),
            tmdb_id = Id<TmdbId>(2),
            name = "Test Show 2",
            overview = "Test overview 2",
            language = "en",
            year = "2023-02-01",
            ratings = 7.5,
            vote_count = 200,
            genres = listOf("Comedy", "Romance"),
            status = "Ended",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/test2.jpg",
            backdrop_path = "/backdrop2.jpg",
        )

        val _ = database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(3),
            tmdb_id = Id<TmdbId>(3),
            name = "Test Show 3",
            overview = "Test overview 3",
            language = "en",
            year = "2023-03-01",
            ratings = 9.0,
            vote_count = 300,
            genres = listOf("Drama", "Thriller"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/test3.jpg",
            backdrop_path = "/backdrop3.jpg",
        )
    }
}
