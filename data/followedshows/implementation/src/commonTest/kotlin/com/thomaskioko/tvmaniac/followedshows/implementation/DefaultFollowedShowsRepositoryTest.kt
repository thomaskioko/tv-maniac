package com.thomaskioko.tvmaniac.followedshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.implementation.fixtures.FakeFollowedShowsDataSource
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
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
        entries.first().tmdbId shouldBe 1L
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should re-add show marked for deletion`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
                traktId = 123L,
            ),
        )

        repository.addFollowedShow(1L)

        val entry = dao.entryWithTmdbId(1L)
        entry?.pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should not add show already in watchlist`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
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
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 123L,
            ),
        )

        repository.removeFollowedShow(1L)

        val entry = dao.entryWithTmdbId(1L)
        entry?.pendingAction shouldBe PendingAction.DELETE
    }

    @Test
    fun `should delete local entry given no trakt id`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        repository.removeFollowedShow(1L)

        val entry = dao.entryWithTmdbId(1L)
        entry shouldBe null
    }

    @Test
    fun `should observe followed shows`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        repository.observeFollowedShows().test {
            val entries = awaitItem()
            entries.size shouldBe 2
            entries.map { it.tmdbId }.toSet() shouldBe setOf(1L, 2L)
            cancelAndConsumeRemainingEvents()
        }
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
        fakeAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        repository.syncFollowedShows()

        fakeDataSource.addShowsCallCount shouldBe 1
        fakeDataSource.lastAddedTmdbIds shouldBe listOf(1L, 2L)

        val entries = dao.entries()
        entries.all { it.pendingAction == PendingAction.NOTHING } shouldBe true
    }

    @Test
    fun `should process pending deletes during sync`() = runTest {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
                traktId = 123L,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.DELETE,
                traktId = 456L,
            ),
        )

        repository.syncFollowedShows()

        fakeDataSource.removeShowsCallCount shouldBe 1
        fakeDataSource.lastRemovedTmdbIds shouldBe listOf(1L, 2L)

        val entries = dao.entries()
        entries.size shouldBe 0
    }

    @Test
    fun `should pull remote watchlist during sync`() = runTest {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_IN)
        fakeDataSource.followedShows = listOf(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 111L,
            ),
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 222L,
            ),
        )

        repository.syncFollowedShows()

        val entries = dao.entries()
        entries.size shouldBe 2
        entries.map { it.tmdbId }.toSet() shouldBe setOf(1L, 2L)

        fakeRequestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should sync remote additions and deletions`() = runTest {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 111L,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 222L,
            ),
        )

        fakeDataSource.followedShows = listOf(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 222L,
            ),
            FollowedShowEntry(
                tmdbId = 3L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 333L,
            ),
        )

        repository.syncFollowedShows()

        val entries = dao.entries()
        entries.size shouldBe 2
        entries.map { it.tmdbId }.toSet() shouldBe setOf(2L, 3L)
    }

    @Test
    fun `should preserve pending upload entries after sync processes them`() = runTest {
        fakeAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 222L,
            ),
        )

        fakeDataSource.followedShows = listOf(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 111L,
            ),
            FollowedShowEntry(
                tmdbId = 3L,
                followedAt = testInstant,
                pendingAction = PendingAction.NOTHING,
                traktId = 333L,
            ),
        )

        repository.syncFollowedShows()

        val entries = dao.entries()
        entries.any { it.tmdbId == 1L } shouldBe true
        entries.find { it.tmdbId == 1L }?.pendingAction shouldBe PendingAction.NOTHING
        entries.find { it.tmdbId == 1L }?.traktId shouldBe 111L
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

    private fun insertTestShows() {
        val _ = database.tvShowQueries.upsert(
            id = Id(1),
            name = "Test Show 1",
            overview = "Test overview 1",
            language = "en",
            first_air_date = "2023-01-01",
            vote_average = 8.0,
            vote_count = 100,
            popularity = 95.0,
            genre_ids = listOf(1, 2),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/test1.jpg",
            backdrop_path = "/backdrop1.jpg",
        )

        val _ = database.tvShowQueries.upsert(
            id = Id(2),
            name = "Test Show 2",
            overview = "Test overview 2",
            language = "en",
            first_air_date = "2023-02-01",
            vote_average = 7.5,
            vote_count = 200,
            popularity = 85.0,
            genre_ids = listOf(2, 3),
            status = "Ended",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/test2.jpg",
            backdrop_path = "/backdrop2.jpg",
        )

        val _ = database.tvShowQueries.upsert(
            id = Id(3),
            name = "Test Show 3",
            overview = "Test overview 3",
            language = "en",
            first_air_date = "2023-03-01",
            vote_average = 9.0,
            vote_count = 300,
            popularity = 75.0,
            genre_ids = listOf(1, 3),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/test3.jpg",
            backdrop_path = "/backdrop3.jpg",
        )
    }
}
