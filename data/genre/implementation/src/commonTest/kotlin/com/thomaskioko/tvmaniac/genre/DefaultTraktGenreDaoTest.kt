package com.thomaskioko.tvmaniac.genre

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
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
internal class DefaultTraktGenreDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: TraktGenreDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultTraktGenreDao(database, showIdResolver, dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should order paged shows by page then page order given two pages exist`() = runTest {
        addShow(tmdbId = 1L, name = "Show 1")
        addShow(tmdbId = 2L, name = "Show 2")
        addShow(tmdbId = 3L, name = "Show 3")
        dao.upsertGenreShow(genreSlug = "drama", showId = 2L, pageOrder = 1L, category = "POPULAR", page = 1L)
        dao.upsertGenreShow(genreSlug = "drama", showId = 1L, pageOrder = 0L, category = "POPULAR", page = 1L)
        dao.upsertGenreShow(genreSlug = "drama", showId = 3L, pageOrder = 0L, category = "POPULAR", page = 2L)

        val result = dao.getPagedShowsByGenreSlugAndCategory(slug = "drama", category = "POPULAR").load(
            LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        ) as LoadResult.Page<Int, ShowEntity>

        result.data.map { it.tmdbId } shouldBe listOf(1L, 2L, 3L)
    }

    @Test
    fun `should check if a page exists`() {
        addShow(tmdbId = 1L, name = "Show 1")
        dao.upsertGenreShow(genreSlug = "drama", showId = 1L, pageOrder = 0L, category = "POPULAR", page = 1L)

        dao.pageExists(slug = "drama", category = "POPULAR", page = 1L) shouldBe true
        dao.pageExists(slug = "drama", category = "POPULAR", page = 2L) shouldBe false
    }

    @Test
    fun `should return only page 1 rows given the genre row query`() = runTest {
        dao.upsertGenre(slug = "drama", name = "Drama")
        addShow(tmdbId = 1L, name = "Show 1")
        addShow(tmdbId = 2L, name = "Show 2")
        dao.upsertGenreShow(genreSlug = "drama", showId = 1L, pageOrder = 0L, category = "POPULAR", page = 1L)
        dao.upsertGenreShow(genreSlug = "drama", showId = 2L, pageOrder = 0L, category = "POPULAR", page = 2L)

        dao.observeGenresWithShowsByCategory(category = "POPULAR").test {
            val rows = awaitItem()
            rows.single().shows.map { it.tmdbId } shouldBe listOf(1L)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should leave other pages given a page scoped delete`() {
        addShow(tmdbId = 1L, name = "Show 1")
        addShow(tmdbId = 2L, name = "Show 2")
        dao.upsertGenreShow(genreSlug = "drama", showId = 1L, pageOrder = 0L, category = "POPULAR", page = 1L)
        dao.upsertGenreShow(genreSlug = "drama", showId = 2L, pageOrder = 0L, category = "POPULAR", page = 2L)

        dao.deleteShowsByGenreSlugCategoryAndPage(slug = "drama", category = "POPULAR", page = 1L)

        dao.pageExists(slug = "drama", category = "POPULAR", page = 1L) shouldBe false
        dao.pageExists(slug = "drama", category = "POPULAR", page = 2L) shouldBe true
    }

    private fun addShow(tmdbId: Long, name: String) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = name,
            overview = "$name overview",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$name.jpg",
            backdrop_path = null,
        )
    }
}
