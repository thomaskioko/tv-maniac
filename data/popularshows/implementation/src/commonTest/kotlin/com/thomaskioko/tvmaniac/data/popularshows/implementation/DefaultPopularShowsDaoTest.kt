package com.thomaskioko.tvmaniac.data.popularshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Popular_shows
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
internal class DefaultPopularShowsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: PopularShowsDao

    private val popularShowsQueries
        get() = database.popularShowsQueries

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultPopularShowsDao(database, coroutineDispatcher)
        insertTestShows()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should insert popular shows`() = runTest {
        // Given - first insert a show into tvshow table
        database.tvShowQueries.upsert(
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

        val popularShow = Popular_shows(
            id = Id(999),
            page = Id(1),
            name = "New Test Show",
            poster_path = "/new_test.jpg",
            overview = "New test overview",
            page_order = 0,
        )

        // When
        dao.upsert(popularShow)

        // Then
        val count = popularShowsQueries.count().executeAsOne()
        count shouldBe 3L // 2 existing + 1 new

        // Verify it appears in the full query result
        dao.observePopularShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 3
            shows.any { it.id == 999L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update popular show item`() = runTest {
        // Given - show already exists from setup
        val existingShow = Popular_shows(
            id = Id(1),
            page = Id(2), // Different page
            name = "Test Show 1 Updated",
            poster_path = "/test1_updated.jpg",
            overview = "Updated test overview",
            page_order = 0,
        )

        // When
        dao.upsert(existingShow)

        // Then - observePopularShows returns ALL shows, not filtered by page
        dao.observePopularShows(page = 2).test {
            val shows = awaitItem()
            shows.size shouldBe 1 // Still returns all shows
            val updatedShow = shows.find { it.id == 1L }
            updatedShow?.page shouldBe 2L
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update popular show item in list and return updated list`() = runTest {
        // Given - shows already exist from setup

        // When - observe the list and update a show
        dao.observePopularShows(page = 1).test {
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            // Update one of the shows to a different page
            val updatedShow = Popular_shows(
                id = Id(1),
                page = Id(3),
                name = "Test Show 1 Updated",
                poster_path = "/test1_updated.jpg",
                overview = "Updated test overview",
                page_order = 0,
            )
            dao.upsert(updatedShow)

            cancelAndConsumeRemainingEvents()
        }

        // Verify the show was updated (observePopularShows returns ALL shows regardless of page parameter)
        dao.observePopularShows(page = 3).test {
            val shows = awaitItem()
            shows.size shouldBe 1
            val updatedShow = shows.find { it.id == 1L }
            updatedShow?.page shouldBe 3L
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should delete popular show by id`() {
        // Given - shows exist from setup
        val initialCount = popularShowsQueries.count().executeAsOne()
        initialCount shouldBe 2L

        // When
        dao.deletePopularShow(1L)

        // Then
        val finalCount = popularShowsQueries.count().executeAsOne()
        finalCount shouldBe 1L
    }

    @Test
    fun `should delete all popular shows`() {
        // Given - shows exist from setup
        val initialCount = popularShowsQueries.count().executeAsOne()
        initialCount shouldBe 2L

        // When
        dao.deletePopularShows()

        // Then
        val finalCount = popularShowsQueries.count().executeAsOne()
        finalCount shouldBe 0L
    }

    @Test
    fun `should check if page exists`() {
        // Given - page 1 exists from setup

        // When & Then
        dao.pageExists(1L) shouldBe true
        dao.pageExists(999L) shouldBe false
    }

    @Test
    fun `should observe popular shows for specific page`() = runTest {
        // Given - shows exist from setup

        // When & Then - observePopularShows returns ALL shows regardless of page parameter
        dao.observePopularShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 2
            // All shows should be from page 1 since that's what we inserted in setup
            shows.all { it.page == 1L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should observe popular shows`() = runTest {
        // Given - shows exist from setup with show data populated

        // When & Then
        dao.observePopularShows(page = 1).test {
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
        popularShowsQueries.insert(
            id = Id(999),
            page = Id(1),
            name = null,
            poster_path = "/test999.jpg",
            overview = "Test overview 999",
            page_order = 0,
        )

        // When & Then
        dao.observePopularShows(page = 1).test {
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
        popularShowsQueries.insert(
            id = Id(999),
            page = Id(2),
            name = "Page 2 Show",
            poster_path = "/page2.jpg",
            overview = "Page 2 overview",
            page_order = 0,
        )

        // When & Then
        dao.observePopularShows(page = 1).test {
            val page1Shows = awaitItem()
            page1Shows.size shouldBe 2 // Only page 1 shows
            page1Shows.all { it.page == 1L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }

        dao.observePopularShows(page = 2).test {
            val page2Shows = awaitItem()
            page2Shows.size shouldBe 1 // Only page 2 show
            page2Shows.all { it.page == 2L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should handle empty results`() = runTest {
        // Given - clear all data
        dao.deletePopularShows()

        // When & Then
        dao.observePopularShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 0
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should be reactive to data changes`() = runTest {
        // Given - initial state from setup

        dao.observePopularShows(page = 1).test {
            // Initial shows
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            // When - add a new show
            val newShow = Popular_shows(
                id = Id(999),
                page = Id(1),
                name = "New Reactive Show",
                poster_path = "/reactive.jpg",
                overview = "Reactive overview",
                page_order = 0,
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
    fun `should handle COALESCE for empty names correctly`() = runTest {
        database.popularShowsQueries.transaction {
            database.popularShowsQueries.insert(
                id = Id(888),
                page = Id(1),
                name = "",
                poster_path = "/empty.jpg",
                overview = "Empty name test",
                page_order = 0,
            )
        }

        dao.observePopularShows(page = 1).test {
            val shows = awaitItem()
            val emptyNameShow = shows.find { it.id == 888L }
            emptyNameShow?.title shouldBe "" // COALESCE should return empty string
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun insertTestShows() {
        database.tvShowQueries.upsert(
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

        database.tvShowQueries.upsert(
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

        popularShowsQueries.insert(
            id = Id(1),
            page = Id(1),
            name = "Test Show 1",
            poster_path = "/test1.jpg",
            overview = "Test overview 1",
            page_order = 0,
        )

        popularShowsQueries.insert(
            id = Id(2),
            page = Id(1),
            name = "Test Show 2",
            poster_path = "/test2.jpg",
            overview = "Test overview 2",
            page_order = 0,
        )
    }
}
