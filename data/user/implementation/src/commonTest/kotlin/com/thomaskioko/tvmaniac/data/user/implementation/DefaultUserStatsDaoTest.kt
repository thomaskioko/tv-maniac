package com.thomaskioko.tvmaniac.data.user.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.api.model.UserWatchTime
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultUserStatsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: DefaultUserStatsDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultUserStatsDao(database, coroutineDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should return null when no stats exist`() = runTest {
        dao.observeUserProfileStats("test-user").test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for zero minutes`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 0,
            episodesWatched = 0,
            minutesWatched = 0,
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 0
            stats?.episodesWatched shouldBe 0
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 0,
                days = 0,
                hours = 0,
                minutes = 0,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for minutes only`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 1,
            episodesWatched = 2,
            minutesWatched = 45,
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 1
            stats?.episodesWatched shouldBe 2
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 0,
                days = 0,
                hours = 0,
                minutes = 45,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for hours and minutes`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 5,
            episodesWatched = 50,
            minutesWatched = 135, // 2 hours 15 minutes
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 5
            stats?.episodesWatched shouldBe 50
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 0,
                days = 0,
                hours = 2,
                minutes = 15,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for days hours and minutes`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 20,
            episodesWatched = 200,
            minutesWatched = 4320, // 3 days (1440 * 3)
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 20
            stats?.episodesWatched shouldBe 200
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 0,
                days = 3,
                hours = 0,
                minutes = 0,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for complex duration`() = runTest {
        // 2 days, 5 hours, 30 minutes = (2 * 1440) + (5 * 60) + 30 = 3210 minutes
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 15,
            episodesWatched = 150,
            minutesWatched = 3210,
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 15
            stats?.episodesWatched shouldBe 150
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 0,
                days = 2,
                hours = 5,
                minutes = 30,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for years`() = runTest {
        // 1 year = 525600 minutes
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 100,
            episodesWatched = 1000,
            minutesWatched = 525600,
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 100
            stats?.episodesWatched shouldBe 1000
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 1,
                days = 0,
                hours = 0,
                minutes = 0,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch time breakdown for years with remainder`() = runTest {
        // 1 year, 30 days, 12 hours, 45 minutes
        // = 525600 + (30 * 1440) + (12 * 60) + 45 = 569565 minutes
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 150,
            episodesWatched = 1500,
            minutesWatched = 569565,
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 150
            stats?.episodesWatched shouldBe 1500
            stats?.userWatchTime shouldBe UserWatchTime(
                years = 1,
                days = 30,
                hours = 12,
                minutes = 45,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update stats when upserting for same slug`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 10,
            episodesWatched = 100,
            minutesWatched = 6000,
        )

        dao.upsertStats(
            slug = "test-user",
            showsWatched = 20,
            episodesWatched = 200,
            minutesWatched = 12000,
        )

        dao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 20
            stats?.episodesWatched shouldBe 200
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should delete all stats`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 10,
            episodesWatched = 100,
            minutesWatched = 6000,
        )

        dao.deleteAll()

        dao.observeUserProfileStats("test-user").test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return stats for correct slug only`() = runTest {
        dao.upsertStats(
            slug = "user-1",
            showsWatched = 10,
            episodesWatched = 100,
            minutesWatched = 6000,
        )
        dao.upsertStats(
            slug = "user-2",
            showsWatched = 20,
            episodesWatched = 200,
            minutesWatched = 12000,
        )

        dao.observeUserProfileStats("user-1").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 10
            stats?.episodesWatched shouldBe 100
            cancelAndConsumeRemainingEvents()
        }

        dao.observeUserProfileStats("user-2").test {
            val stats = awaitItem()
            stats?.showsWatched shouldBe 20
            stats?.episodesWatched shouldBe 200
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should observe raw stats`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 10,
            episodesWatched = 100,
            minutesWatched = 6000,
        )

        dao.observeUserStats("test-user").test {
            val stats = awaitItem()
            stats?.shows_watched shouldBe 10
            stats?.episodes_watched shouldBe 100
            stats?.minutes_watched shouldBe 6000
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should get stats synchronously`() = runTest {
        dao.upsertStats(
            slug = "test-user",
            showsWatched = 10,
            episodesWatched = 100,
            minutesWatched = 6000,
        )

        val stats = dao.getUserStats("test-user")
        stats?.shows_watched shouldBe 10
        stats?.episodes_watched shouldBe 100
        stats?.minutes_watched shouldBe 6000
    }
}
