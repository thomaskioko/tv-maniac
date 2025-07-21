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
        )

        // When
        dao.upsert(existingShow)

        // Then - observePopularShows returns ALL shows, not filtered by page
        dao.observePopularShows(page = 2).test {
            val shows = awaitItem()
            shows.size shouldBe 2 // Still returns all shows
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
            )
            dao.upsert(updatedShow)

            cancelAndConsumeRemainingEvents()
        }

        // Verify the show was updated (observePopularShows returns ALL shows regardless of page parameter)
        dao.observePopularShows(page = 3).test {
            val shows = awaitItem()
            shows.size shouldBe 2 // Still returns all shows
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

    private fun insertTestShows() {
        // Insert test TV shows first
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

        // Insert popular shows
        popularShowsQueries.insert(
            id = Id(1),
            page = Id(1),
        )

        popularShowsQueries.insert(
            id = Id(2),
            page = Id(1),
        )
    }
}
