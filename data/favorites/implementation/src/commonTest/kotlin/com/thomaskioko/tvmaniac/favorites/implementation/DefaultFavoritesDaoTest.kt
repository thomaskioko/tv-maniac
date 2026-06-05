package com.thomaskioko.tvmaniac.favorites.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesDao
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultFavoritesDaoTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: FavoritesDao

    @BeforeTest
    fun setUp() {
        dao = DefaultFavoritesDao(database, showIdResolver, dispatchers)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should emit favorite show joined with show metadata`() = runTest(testDispatcher) {
        insertShow(id = 1, name = "Breaking Bad")
        dao.upsert(traktId = 1, rank = 0, listedAt = "2020-01-01T00:00:00Z")

        dao.observeFavoriteShows().test {
            awaitItem() shouldContainExactly listOf(expectedShow(1, "Breaking Bad"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should order favorites by rank ascending`() = runTest(testDispatcher) {
        insertShow(id = 1, name = "First Rank")
        insertShow(id = 2, name = "Second Rank")
        dao.upsert(traktId = 2, rank = 1, listedAt = "2020-01-02T00:00:00Z")
        dao.upsert(traktId = 1, rank = 0, listedAt = "2020-01-01T00:00:00Z")

        dao.observeFavoriteShows().test {
            awaitItem() shouldContainExactly listOf(
                expectedShow(1, "First Rank"),
                expectedShow(2, "Second Rank"),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit empty list after deleteAll`() = runTest(testDispatcher) {
        insertShow(id = 1, name = "Breaking Bad")
        dao.upsert(traktId = 1, rank = 0, listedAt = "2020-01-01T00:00:00Z")

        dao.observeFavoriteShows().test {
            awaitItem() shouldContainExactly listOf(expectedShow(1, "Breaking Bad"))

            dao.deleteAll()

            awaitItem().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun expectedShow(id: Long, title: String): FavoriteShow =
        FavoriteShow(traktId = id, tmdbId = id, title = title, posterPath = "/$id.jpg", year = "2020-01-01")

    private fun insertShow(id: Long, name: String) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(id),
            name = name,
            overview = "Overview for $name",
            language = "en",
            year = "2020-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = null,
        )
        showIdForTraktId(id)
    }
}
