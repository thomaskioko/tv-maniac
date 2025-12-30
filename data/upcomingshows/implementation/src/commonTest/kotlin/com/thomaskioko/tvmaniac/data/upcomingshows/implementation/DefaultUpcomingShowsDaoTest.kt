package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Upcoming_shows
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
internal class DefaultUpcomingShowsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: UpcomingShowsDao

    private val upcomingShowsQueries
        get() = database.upcomingShowsQueries

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultUpcomingShowsDao(database, coroutineDispatcher)
        insertTestShows()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should insert upcoming shows`() = runTest {
        // Given - first insert a show into tvshow table
        val _ = database.tvShowQueries.upsert(
            id = Id(999),
            name = "New Test Show",
            overview = "New test overview",
            language = "en",
            first_air_date = "2023-03-01",
            vote_average = 9.0,
            vote_count = 300,
            popularity = 99.0,
            genre_ids = listOf(1, 2),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/new_test.jpg",
            backdrop_path = "/new_backdrop.jpg",
        )

        val upcomingShow = Upcoming_shows(
            id = Id(999),
            page = Id(1),
            name = "New Test Show",
            poster_path = "/new_test.jpg",
            overview = "New test overview",
        )

        // When
        dao.upsert(upcomingShow)

        // Then
        dao.observeUpcomingShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 3
            shows.any { it.id == 999L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should observe upcoming shows using stable query method`() = runTest {
        // Given - shows exist from setup with show data populated

        // When & Then
        dao.observeUpcomingShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 2

            // Verify show data is correctly returned from stable query
            val show1 = shows.find { it.id == 1L }
            show1?.title shouldBe "Test Show 1"
            show1?.posterPath shouldBe "/test1.jpg"
            show1?.overview shouldBe "Test overview 1"
            show1?.inLibrary shouldBe false // Always false from stable query

            val show2 = shows.find { it.id == 2L }
            show2?.title shouldBe "Test Show 2"
            show2?.posterPath shouldBe "/test2.jpg"
            show2?.overview shouldBe "Test overview 2"
            show2?.inLibrary shouldBe false // Always false from stable query

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `stable query should not return shows with null names`() = runTest {
        // Given - insert a show without name (simulating pre-migration data)
        val _ = upcomingShowsQueries.insert(
            id = Id(999),
            page = Id(1),
            name = null,
            poster_path = "/test999.jpg",
            overview = "Test overview 999",
        )

        // When & Then
        dao.observeUpcomingShows(page = 1).test {
            val shows = awaitItem()
            // Should only return shows with non-null names (the 2 from setup)
            shows.size shouldBe 2
            shows.none { it.id == 999L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `stable query should filter by page correctly`() = runTest {
        // Given - add shows to different pages
        val _ = upcomingShowsQueries.insert(
            id = Id(999),
            page = Id(2),
            name = "Page 2 Show",
            poster_path = "/page2.jpg",
            overview = "Page 2 overview",
        )

        // When & Then
        dao.observeUpcomingShows(page = 1).test {
            val page1Shows = awaitItem()
            page1Shows.size shouldBe 2 // Only page 1 shows
            page1Shows.all { it.page == 1L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }

        dao.observeUpcomingShows(page = 2).test {
            val page2Shows = awaitItem()
            page2Shows.size shouldBe 1 // Only page 2 show
            page2Shows.all { it.page == 2L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `stable query should handle empty results`() = runTest {
        // Given - clear all data
        dao.deleteUpcomingShows()

        // When & Then
        dao.observeUpcomingShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 0
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should check if page exists`() {
        // Given - page 1 exists from setup

        // When & Then
        dao.pageExists(1L) shouldBe true
        dao.pageExists(999L) shouldBe false
    }

    @Test
    fun `stable query should be reactive to data changes`() = runTest {
        // Given - initial state from setup

        dao.observeUpcomingShows(page = 1).test {
            // Initial shows
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            // When - add a new show
            val newShow = Upcoming_shows(
                id = Id(999),
                page = Id(1),
                name = "New Reactive Show",
                poster_path = "/reactive.jpg",
                overview = "Reactive overview",
            )
            dao.upsert(newShow)

            // Then - should emit updated list
            val updatedShows = awaitItem()
            updatedShows.size shouldBe 3
            updatedShows.any { it.id == 999L && it.title == "New Reactive Show" } shouldBe true

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should delete upcoming show by id`() = runTest {
        // Given - shows exist from setup
        dao.observeUpcomingShows(page = 1).test {
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            // When
            dao.deleteUpcomingShow(1L)

            // Then
            val updatedShows = awaitItem()
            updatedShows.size shouldBe 1
            updatedShows.none { it.id == 1L } shouldBe true

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should delete all upcoming shows`() = runTest {
        dao.observeUpcomingShows(page = 1).test {
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            // When
            dao.deleteUpcomingShows()

            // Then
            val emptyShows = awaitItem()
            emptyShows.size shouldBe 0

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `stable query should handle COALESCE for empty names correctly`() = runTest {
        database.upcomingShowsQueries.transaction {
            val _ = database.upcomingShowsQueries.insert(
                id = Id(888),
                page = Id(1),
                name = "",
                poster_path = "/empty.jpg",
                overview = "Empty name test",
            )
        }

        // When & Then
        dao.observeUpcomingShows(page = 1).test {
            val shows = awaitItem()
            // Should include the show with empty name due to COALESCE
            val emptyNameShow = shows.find { it.id == 888L }
            emptyNameShow?.title shouldBe "" // COALESCE should return empty string
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun insertTestShows() {
        // Insert test TV shows first
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

        // Insert upcoming shows with show data
        val _ = upcomingShowsQueries.insert(
            id = Id(1),
            page = Id(1),
            name = "Test Show 1",
            poster_path = "/test1.jpg",
            overview = "Test overview 1",
        )

        val _ = upcomingShowsQueries.insert(
            id = Id(2),
            page = Id(1),
            name = "Test Show 2",
            poster_path = "/test2.jpg",
            overview = "Test overview 2",
        )
    }
}
