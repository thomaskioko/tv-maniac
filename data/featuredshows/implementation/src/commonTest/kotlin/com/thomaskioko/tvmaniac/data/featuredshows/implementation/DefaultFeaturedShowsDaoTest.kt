package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.Id
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
internal class DefaultFeaturedShowsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: FeaturedShowsDao

    private val featuredShowsQueries
        get() = database.featuredShowsQueries

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultFeaturedShowsDao(database, coroutineDispatcher)
        insertTestShows()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should insert featured shows`() = runTest {
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

        val featuredShow = Featured_shows(
            id = Id(999),
            name = "New Test Show",
            poster_path = "/new_test.jpg",
            overview = "New test overview",
            page_order = 0,
        )

        dao.upsert(featuredShow)

        dao.observeFeaturedShows(page = 1).test {
            val shows = awaitItem()
            shows.size shouldBe 3
            shows.any { it.id == 999L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should observe featured shows using method`() = runTest {
        dao.observeFeaturedShows(1).test {
            val shows = awaitItem()
            shows.size shouldBe 2

            val show1 = shows.find { it.id == 1L }
            show1?.title shouldBe "Test Show 1"
            show1?.posterPath shouldBe "/test1.jpg"
            show1?.overview shouldBe "Test overview 1"
            show1?.inLibrary shouldBe false

            val show2 = shows.find { it.id == 2L }
            show2?.title shouldBe "Test Show 2"
            show2?.posterPath shouldBe "/test2.jpg"
            show2?.overview shouldBe "Test overview 2"
            show2?.inLibrary shouldBe false

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not return shows with null names`() = runTest {
        featuredShowsQueries.insert(
            id = Id(999),
            name = null,
            poster_path = "/test999.jpg",
            overview = "Test overview 999",
            page_order = 0,
        )

        dao.observeFeaturedShows(1).test {
            val shows = awaitItem()
            shows.size shouldBe 2
            shows.none { it.id == 999L } shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should handle empty results`() = runTest {
        dao.deleteFeaturedShows()

        dao.observeFeaturedShows(1).test {
            val shows = awaitItem()
            shows.size shouldBe 0
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should be reactive to data changes`() = runTest {
        dao.observeFeaturedShows(1).test {
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            val newShow = Featured_shows(
                id = Id(999),
                name = "New Reactive Show",
                poster_path = "/reactive.jpg",
                overview = "Reactive overview",
                page_order = 2,
            )
            dao.upsert(newShow)

            val updatedShows = awaitItem()
            updatedShows.size shouldBe 3
            updatedShows.any { it.id == 999L && it.title == "New Reactive Show" } shouldBe true

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should delete featured shows`() = runTest {
        dao.observeFeaturedShows(1).test {
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            dao.deleteFeaturedShows()

            val emptyShows = awaitItem()
            emptyShows.size shouldBe 0

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should delete featured show by id`() = runTest {
        dao.observeFeaturedShows(1).test {
            val initialShows = awaitItem()
            initialShows.size shouldBe 2

            dao.deleteFeaturedShows(1L)

            val updatedShows = awaitItem()
            updatedShows.size shouldBe 1
            updatedShows.none { it.id == 1L } shouldBe true

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

        featuredShowsQueries.insert(
            id = Id(1),
            name = "Test Show 1",
            poster_path = "/test1.jpg",
            overview = "Test overview 1",
            page_order = 0,
        )

        featuredShowsQueries.insert(
            id = Id(2),
            name = "Test Show 2",
            poster_path = "/test2.jpg",
            overview = "Test overview 2",
            page_order = 1,
        )
    }
}
