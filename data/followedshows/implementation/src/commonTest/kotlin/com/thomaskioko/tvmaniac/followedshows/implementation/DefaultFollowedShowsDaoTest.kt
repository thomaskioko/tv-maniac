package com.thomaskioko.tvmaniac.followedshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultFollowedShowsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val dateTimeProvider: FakeDateTimeProvider = FakeDateTimeProvider()
    private lateinit var dao: FollowedShowsDao

    @BeforeTest
    fun setup() {
        dao = DefaultFollowedShowsDao(database, coroutineDispatcher)
        insertTestShows()
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should upsert followed show entry`() {
        val entry = FollowedShowEntry(
            tmdbId = 1L,
            followedAt = Clock.System.now(),
            pendingAction = PendingAction.UPLOAD,
        )

        val resultId = dao.upsert(entry)

        resultId shouldBe 1L
        val entries = dao.entries()
        entries.size shouldBe 1
        entries.first().tmdbId shouldBe 1L
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should update existing entry on upsert`() {
        val entry = FollowedShowEntry(
            tmdbId = 1L,
            followedAt = Clock.System.now(),
            pendingAction = PendingAction.UPLOAD,
        )

        val id = dao.upsert(entry)

        val updatedEntry =
            entry.copy(id = id, pendingAction = PendingAction.NOTHING, traktId = 12345L)
        val _ = dao.upsert(updatedEntry)

        val entries = dao.entries()
        entries.size shouldBe 1
        entries.first().pendingAction shouldBe PendingAction.NOTHING
        entries.first().traktId shouldBe 12345L
    }

    @Test
    fun `should get all entries`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        val entries = dao.entries()

        entries.size shouldBe 2
        entries.map { it.tmdbId }.toSet() shouldBe setOf(1L, 2L)
    }

    @Test
    fun `should observe entries`() = runTest {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        dao.entriesObservable().test {
            val entries = awaitItem()
            entries.size shouldBe 1
            entries.first().tmdbId shouldBe 1L

            val _ = dao.upsert(
                FollowedShowEntry(
                    tmdbId = 2L,
                    followedAt = Clock.System.now(),
                    pendingAction = PendingAction.UPLOAD,
                ),
            )

            val updatedEntries = awaitItem()
            updatedEntries.size shouldBe 2

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should get entry by show id`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        val entry = dao.entryWithTmdbId(1L)

        entry.shouldNotBeNull()
        entry.tmdbId shouldBe 1L
        entry.pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should return null given show id does not exist`() {
        val entry = dao.entryWithTmdbId(999L)

        entry.shouldBeNull()
    }

    @Test
    fun `should get entries with no pending action`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 3L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        val entries = dao.entriesWithNoPendingAction()

        entries.size shouldBe 2
        entries.map { it.tmdbId }.toSet() shouldBe setOf(1L, 3L)
        entries.all { it.pendingAction == PendingAction.NOTHING } shouldBe true
    }

    @Test
    fun `should get entries with upload pending action`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.UPLOAD,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 3L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        val entries = dao.entriesWithUploadPendingAction()

        entries.size shouldBe 2
        entries.map { it.tmdbId }.toSet() shouldBe setOf(1L, 3L)
        entries.all { it.pendingAction == PendingAction.UPLOAD } shouldBe true
    }

    @Test
    fun `should get entries with delete pending action`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.DELETE,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 3L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.DELETE,
            ),
        )

        val entries = dao.entriesWithDeletePendingAction()

        entries.size shouldBe 2
        entries.map { it.tmdbId }.toSet() shouldBe setOf(1L, 3L)
        entries.all { it.pendingAction == PendingAction.DELETE } shouldBe true
    }

    @Test
    fun `should update pending action`() {
        val id = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.UPLOAD,
            ),
        )

        dao.updatePendingAction(id, PendingAction.NOTHING)

        val entry = dao.entryWithTmdbId(1L)
        entry.shouldNotBeNull()
        entry.pendingAction shouldBe PendingAction.NOTHING
    }

    @Test
    fun `should delete by id`() {
        val id = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        dao.deleteById(id)

        val entries = dao.entries()
        entries.size shouldBe 1
        entries.first().tmdbId shouldBe 2L
    }

    @Test
    fun `should delete by show id`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 2L,
                followedAt = Clock.System.now(),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        dao.deleteByTmdbId(1L)

        val entries = dao.entries()
        entries.size shouldBe 1
        entries.first().tmdbId shouldBe 2L
    }

    @Test
    fun `should preserve followed at timestamp`() {
        val now = dateTimeProvider.now()
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = now,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        val entry = dao.entryWithTmdbId(1L)

        entry.shouldNotBeNull()
        entry.followedAt.toEpochMilliseconds() shouldBe now.toEpochMilliseconds()
    }

    @Test
    fun `should preserve trakt id`() {
        val _ = dao.upsert(
            FollowedShowEntry(
                tmdbId = 1L,
                followedAt = Clock.System.now(),
                traktId = 98765L,
                pendingAction = PendingAction.NOTHING,
            ),
        )

        val entry = dao.entryWithTmdbId(1L)

        entry.shouldNotBeNull()
        entry.traktId shouldBe 98765L
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
