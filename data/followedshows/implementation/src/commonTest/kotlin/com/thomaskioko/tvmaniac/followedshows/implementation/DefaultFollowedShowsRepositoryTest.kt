package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
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
    private val fakeDateTimeProvider: FakeDateTimeProvider = FakeDateTimeProvider()
    private lateinit var transactionRunner: DatabaseTransactionRunner

    @BeforeTest
    fun setup() {
        dao = DefaultFollowedShowsDao(database, coroutineDispatcher)
        transactionRunner = DbTransactionRunner(database)
        repository = DefaultFollowedShowsRepository(
            followedShowsDao = dao,
            transactionRunner = transactionRunner,
            dateTimeProvider = fakeDateTimeProvider,
            dispatchers = coroutineDispatcher,
            logger = FakeLogger(),
        )
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
    fun `should delete local entry given pending upload`() = runTest {
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
    fun `should get followed shows`() = runTest {
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
}
