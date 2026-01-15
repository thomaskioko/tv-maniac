package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.PageId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.MockData.getShow
import com.thomaskioko.tvmaniac.shows.api.MockData.showList
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val trendingShowsQueries
        get() = database.trendingShowsQueries

    private val tvShowQueries
        get() = database.tvShowQueries

    @Test
    fun `should return shows when data is available`() {
        val shows = showList()

        shows.forEachIndexed { index, show ->
            show.insertTvShowQuery()
            trendingShowsQueries.insert(
                traktId = show.trakt_id,
                tmdbId = show.tmdb_id,
                page = Id<PageId>(1),
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
                position = index.toLong(),
            )
        }

        val entities = trendingShowsQueries.trendingShowsByPage(Id<PageId>(1)).executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun `should return show when data is available`() {
        val show = getShow()
        show.insertTvShowQuery()

        val entity = tvShowQueries.tvshowDetails(show.trakt_id).executeAsOne()

        entity shouldNotBe null
        entity.name shouldBe show.name
        entity.overview shouldBe getShow().overview
        entity.ratings shouldBe show.ratings
        entity.backdrop_path shouldBe show.backdrop_path
    }

    @Test
    fun `should return exact match when query matches start of show name`() {
        insertTestShows()
        val query = "Breaking"

        val results = tvShowQueries.searchShows(
            // Parameters for WHERE clause
            query,
            query,
            query,
            query,
            // Parameters for ORDER BY clause
            query,
            query,
            query,
        ).executeAsList()

        results shouldHaveSize 1
        val show = results.first()
        show.name shouldBe "Breaking Bad"
        show.overview shouldBe "A high school chemistry teacher turned meth dealer"
    }

    @Test
    fun `should return results when query matches case insensitive`() {
        insertTestShows()
        val query = "breaking"

        val results = tvShowQueries.searchShows(
            // Parameters for WHERE clause
            query,
            query,
            query,
            query,
            // Parameters for ORDER BY clause
            query,
            query,
            query,
        ).executeAsList()

        results shouldHaveSize 1
        results.first().name shouldBe "Breaking Bad"
    }

    @Test
    fun `should return shows when query has partial a match`() {
        insertTestShows()
        val query = "bad"

        val results = tvShowQueries.searchShows(
            // Parameters for WHERE clause
            query,
            query,
            query,
            query,
            // Parameters for ORDER BY clause
            query,
            query,
            query,
        ).executeAsList()

        results shouldHaveSize 1
        results.first().name shouldBe "Breaking Bad"
    }

    @Test
    fun `should return empty list when no shows match query`() {
        insertTestShows()
        val query = "nonexistent"

        val results = tvShowQueries.searchShows(
            // Parameters for WHERE clause
            query,
            query,
            query,
            query,
            // Parameters for ORDER BY clause
            query,
            query,
            query,
        ).executeAsList()

        results shouldHaveSize 0
    }

    @Test
    fun `should return shows when query start match first`() {
        insertTestShows()
        val query = "the"

        val results = tvShowQueries.searchShows(
            // Parameters for WHERE clause
            query,
            query,
            query,
            query,
            // Parameters for ORDER BY clause
            query,
            query,
            query,
        ).executeAsList()

        results shouldHaveSize 3
        results.get(0).name shouldBe "The Walking Dead"
        results.get(1).name shouldBe "Theory of Everything"
        results.get(2).name shouldBe "In the Dark"
    }

    private fun insertTestShows() {
        listOf(
            TestShow(
                traktId = 1,
                tmdbId = 1,
                name = "Breaking Bad",
                overview = "A high school chemistry teacher turned meth dealer",
            ),
            TestShow(
                traktId = 2,
                tmdbId = 2,
                name = "The Walking Dead",
                overview = "Zombie apocalypse drama",
            ),
            TestShow(
                traktId = 3,
                tmdbId = 3,
                name = "In the Dark",
                overview = "Crime drama series",
            ),
            TestShow(
                traktId = 4,
                tmdbId = 4,
                name = "Theory of Everything",
                overview = "Crime drama series",
            ),
        ).forEach { show ->
            tvShowQueries.upsert(
                trakt_id = Id<TraktId>(show.traktId),
                tmdb_id = Id<TmdbId>(show.tmdbId),
                name = show.name,
                overview = show.overview,
                language = "en",
                ratings = 8.5,
                vote_count = 1000,
                genres = listOf("Drama"),
                poster_path = null,
                backdrop_path = null,
                status = null,
                year = null,
                episode_numbers = null,
                season_numbers = null,
            )
        }
    }

    private data class TestShow(
        val traktId: Long,
        val tmdbId: Long,
        val name: String,
        val overview: String,
    )

    private fun Tvshow.insertTvShowQuery() {
        tvShowQueries.upsert(
            trakt_id = trakt_id,
            tmdb_id = tmdb_id,
            name = name,
            overview = overview,
            language = language,
            year = year,
            ratings = ratings,
            vote_count = vote_count,
            genres = genres,
            status = status,
            episode_numbers = episode_numbers,
            season_numbers = season_numbers,
            poster_path = poster_path,
            backdrop_path = backdrop_path,
        )
    }
}
