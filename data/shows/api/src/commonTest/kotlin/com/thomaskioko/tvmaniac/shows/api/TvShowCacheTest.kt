package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trending_shows
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
            Trending_shows(
                id = show.id,
                page = Id(1),
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
                position = index.toLong(),
            )
                .insert()
        }

        val entities = trendingShowsQueries.trendingShowsByPage(Id(1)).executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun `should return show when data is available`() {
        val show = getShow()
        show.insertTvShowQuery()

        val entity = tvShowQueries.tvshowDetails(show.id).executeAsOne()

        entity shouldNotBe null
        entity.name shouldBe show.name
        entity.overview shouldBe getShow().overview
        entity.vote_average shouldBe show.vote_average
        entity.backdrop_path shouldBe show.backdrop_path
        entity.popularity shouldBe show.popularity
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
                id = 1,
                name = "Breaking Bad",
                overview = "A high school chemistry teacher turned meth dealer",
                popularity = 95.5,
            ),
            TestShow(
                id = 2,
                name = "The Walking Dead",
                overview = "Zombie apocalypse drama",
                popularity = 90.0,
            ),
            TestShow(
                id = 3,
                name = "In the Dark",
                overview = "Crime drama series",
                popularity = 88.0,
            ),
            TestShow(
                id = 4,
                name = "Theory of Everything",
                overview = "Crime drama series",
                popularity = 88.0,
            ),
        ).forEach { show ->
            val _ = tvShowQueries.upsert(
                id = Id(show.id),
                name = show.name,
                overview = show.overview,
                language = "en",
                popularity = show.popularity,
                vote_average = 8.5,
                vote_count = 1000,
                genre_ids = listOf(1, 2, 3),
                poster_path = null,
                backdrop_path = null,
                status = null,
                first_air_date = null,
                episode_numbers = null,
                last_air_date = null,
                season_numbers = null,
            )
        }
    }

    private data class TestShow(
        val id: Long,
        val name: String,
        val overview: String,
        val popularity: Double,
    )

    private fun Tvshow.insertTvShowQuery() {
        val _ = tvShowQueries.upsert(
            id = id,
            name = name,
            overview = overview,
            language = language,
            first_air_date = first_air_date,
            vote_average = vote_average,
            vote_count = vote_count,
            popularity = popularity,
            genre_ids = genre_ids,
            status = status,
            episode_numbers = episode_numbers,
            last_air_date = last_air_date,
            season_numbers = season_numbers,
            poster_path = poster_path,
            backdrop_path = backdrop_path,
        )
    }

    private fun Trending_shows.insert() {
        val _ = trendingShowsQueries.insert(
            id = id,
            page = page,
            poster_path = poster_path,
            overview = overview,
            name = name,
            position = position,
        )
    }
}
