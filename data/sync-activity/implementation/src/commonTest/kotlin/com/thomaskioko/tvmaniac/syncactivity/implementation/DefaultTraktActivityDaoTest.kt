package com.thomaskioko.tvmaniac.syncactivity.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

internal class DefaultTraktActivityDaoTest : BaseDatabaseTest() {

    private lateinit var dao: TraktActivityDao

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    @BeforeTest
    fun setup() {
        dao = DefaultTraktActivityDao(database, coroutineDispatcher)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should upsert activity and retrieve by type`() {
        val remoteTimestamp = Clock.System.now()
        val fetchedAt = Clock.System.now()

        dao.upsert(ActivityType.SHOWS_WATCHLISTED, remoteTimestamp, fetchedAt)

        val result = dao.getByActivityType(ActivityType.SHOWS_WATCHLISTED)
        result.shouldNotBeNull()
        result.activityType shouldBe ActivityType.SHOWS_WATCHLISTED
        result.remoteTimestamp.toEpochMilliseconds() shouldBe remoteTimestamp.toEpochMilliseconds()
        result.fetchedAt.toEpochMilliseconds() shouldBe fetchedAt.toEpochMilliseconds()
    }

    @Test
    fun `should update existing activity on upsert`() {
        val initialTimestamp = Clock.System.now()
        val updatedTimestamp = Clock.System.now() + 1.hours
        val fetchedAt = Clock.System.now()

        dao.upsert(ActivityType.SHOWS_WATCHLISTED, initialTimestamp, fetchedAt)
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, updatedTimestamp, fetchedAt)

        val result = dao.getByActivityType(ActivityType.SHOWS_WATCHLISTED)
        result.shouldNotBeNull()
        result.remoteTimestamp.toEpochMilliseconds() shouldBe updatedTimestamp.toEpochMilliseconds()
    }

    @Test
    fun `should return null given activity type does not exist`() {
        val result = dao.getByActivityType(ActivityType.EPISODES_WATCHED)

        result.shouldBeNull()
    }

    @Test
    fun `should get all activities`() {
        val now = Clock.System.now()
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, now, now)
        dao.upsert(ActivityType.SHOWS_FAVORITED, now, now)
        dao.upsert(ActivityType.EPISODES_WATCHED, now, now)

        val results = dao.getAll()

        results.size shouldBe 3
        results.map { it.activityType }.toSet() shouldBe setOf(
            ActivityType.SHOWS_WATCHLISTED,
            ActivityType.SHOWS_FAVORITED,
            ActivityType.EPISODES_WATCHED,
        )
    }

    @Test
    fun `should delete activity by type`() {
        val now = Clock.System.now()
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, now, now)
        dao.upsert(ActivityType.SHOWS_FAVORITED, now, now)

        dao.delete(ActivityType.SHOWS_WATCHLISTED)

        dao.getByActivityType(ActivityType.SHOWS_WATCHLISTED).shouldBeNull()
        dao.getByActivityType(ActivityType.SHOWS_FAVORITED).shouldNotBeNull()
    }

    @Test
    fun `should delete all activities`() {
        val now = Clock.System.now()
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, now, now)
        dao.upsert(ActivityType.SHOWS_FAVORITED, now, now)
        dao.upsert(ActivityType.EPISODES_WATCHED, now, now)

        dao.deleteAll()

        val results = dao.getAll()
        results.size shouldBe 0
    }

    @Test
    fun `should preserve different activity types independently`() {
        val watchlistTimestamp = Clock.System.now()
        val favoritedTimestamp = Clock.System.now() + 1.hours
        val fetchedAt = Clock.System.now()

        dao.upsert(ActivityType.SHOWS_WATCHLISTED, watchlistTimestamp, fetchedAt)
        dao.upsert(ActivityType.SHOWS_FAVORITED, favoritedTimestamp, fetchedAt)

        val watchlistResult = dao.getByActivityType(ActivityType.SHOWS_WATCHLISTED)
        val favoritedResult = dao.getByActivityType(ActivityType.SHOWS_FAVORITED)

        watchlistResult.shouldNotBeNull()
        favoritedResult.shouldNotBeNull()
        watchlistResult.remoteTimestamp.toEpochMilliseconds() shouldBe watchlistTimestamp.toEpochMilliseconds()
        favoritedResult.remoteTimestamp.toEpochMilliseconds() shouldBe favoritedTimestamp.toEpochMilliseconds()
    }

    @Test
    fun `should return false given no stored activity for type`() {
        val result = dao.isDurationExpired(ActivityType.SHOWS_WATCHLISTED)

        result shouldBe false
    }

    @Test
    fun `should return true given activity not yet synced`() {
        val now = Clock.System.now()
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, now, now)

        val result = dao.isDurationExpired(ActivityType.SHOWS_WATCHLISTED)

        result shouldBe true
    }

    @Test
    fun `should return false given activity already synced with same timestamp`() {
        val now = Clock.System.now()
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, now, now)
        dao.markAsSynced(ActivityType.SHOWS_WATCHLISTED)

        val result = dao.isDurationExpired(ActivityType.SHOWS_WATCHLISTED)

        result shouldBe false
    }

    @Test
    fun `should return true given remote timestamp changed after sync`() {
        val initialTimestamp = Clock.System.now()
        val newerTimestamp = initialTimestamp + 1.hours
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, initialTimestamp, initialTimestamp)
        dao.markAsSynced(ActivityType.SHOWS_WATCHLISTED)
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, newerTimestamp, newerTimestamp)

        val result = dao.isDurationExpired(ActivityType.SHOWS_WATCHLISTED)

        result shouldBe true
    }

    @Test
    fun `should mark activity as synced`() {
        val now = Clock.System.now()
        dao.upsert(ActivityType.SHOWS_WATCHLISTED, now, now)

        dao.markAsSynced(ActivityType.SHOWS_WATCHLISTED)

        val activity = dao.getByActivityType(ActivityType.SHOWS_WATCHLISTED)
        activity.shouldNotBeNull()
        activity.syncedRemoteTimestamp shouldBe activity.remoteTimestamp
    }
}
