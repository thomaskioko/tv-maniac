package com.thomaskioko.tvmaniac.syncactivity.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

internal class DefaultActivitySyncRepositoryTest : BaseDatabaseTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val now = Instant.fromEpochMilliseconds(1_700_000_000_000L)
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = now)

    private lateinit var activityDao: TraktActivityDao
    private lateinit var repository: ActivitySyncRepository

    @BeforeTest
    fun setup() {
        activityDao = DefaultTraktActivityDao(database, dispatchers)
        repository = DefaultActivitySyncRepository(
            database = database,
            activityDao = activityDao,
            dateTimeProvider = dateTimeProvider,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should return false given no remote timestamp recorded`() = runTest {
        repository.isAheadOf(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe false
    }

    @Test
    fun `should return true given remote timestamp exists and consumer has no checkpoint`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)

        repository.isAheadOf(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe true
    }

    @Test
    fun `should return false given consumer checkpoint matches remote timestamp`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)
        repository.markSyncedTo(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        )

        repository.isAheadOf(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe false
    }

    @Test
    fun `should return true given remote timestamp advances past consumer checkpoint`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)
        repository.markSyncedTo(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        )

        val newer = now + 1.hours
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = newer, fetchedAt = newer)

        repository.isAheadOf(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe true
    }

    @Test
    fun `should keep checkpoints isolated across consumers`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)
        repository.markSyncedTo(
            ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            ActivityType.EPISODES_WATCHED,
        )

        repository.isAheadOf(
            ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe false
        repository.isAheadOf(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe true
    }

    @Test
    fun `should keep checkpoints isolated across activity types for same consumer`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)
        activityDao.upsert(ActivityType.EPISODES_PAUSED, remoteTimestamp = now, fetchedAt = now)
        repository.markSyncedTo(
            ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            ActivityType.EPISODES_WATCHED,
        )

        repository.isAheadOf(
            ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            ActivityType.EPISODES_WATCHED,
        ) shouldBe false
        repository.isAheadOf(
            ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            ActivityType.EPISODES_PAUSED,
        ) shouldBe true
    }

    @Test
    fun `should no-op markSyncedTo given no remote timestamp recorded`() = runTest {
        repository.markSyncedTo(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        )

        repository.getSyncTimestamp(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ).shouldBeNull()
    }

    @Test
    fun `should return checkpoint timestamp matching the remote timestamp recorded at markSyncedTo`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)
        repository.markSyncedTo(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        )

        val checkpoint = repository.getSyncTimestamp(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        )
        checkpoint?.toEpochMilliseconds() shouldBe now.toEpochMilliseconds()
    }

    @Test
    fun `should truncate every checkpoint given clearAll`() = runTest {
        activityDao.upsert(ActivityType.EPISODES_WATCHED, remoteTimestamp = now, fetchedAt = now)
        activityDao.upsert(ActivityType.SHOWS_WATCHLISTED, remoteTimestamp = now, fetchedAt = now)
        repository.markSyncedTo(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        )
        repository.markSyncedTo(
            ActivitySyncTypes.LIBRARY_WATCHLIST,
            ActivityType.SHOWS_WATCHLISTED,
        )

        repository.clearAll()

        repository.getSyncTimestamp(
            ActivitySyncTypes.BULK_WATCHED_EPISODES,
            ActivityType.EPISODES_WATCHED,
        ).shouldBeNull()
        repository.getSyncTimestamp(
            ActivitySyncTypes.LIBRARY_WATCHLIST,
            ActivityType.SHOWS_WATCHLISTED,
        ).shouldBeNull()
    }
}
