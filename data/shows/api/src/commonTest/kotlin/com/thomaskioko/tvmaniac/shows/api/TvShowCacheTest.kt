package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.PageId
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.MockData.getShow
import com.thomaskioko.tvmaniac.shows.api.MockData.showList
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
            val showId = showIdForTraktId(show.tmdb_id.id)
            trendingShowsQueries.insert(
                showId = showId,
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
        showIdForTraktId(show.tmdb_id.id)

        val entity = tvShowQueries.tvshowDetails(show.tmdb_id.id).executeAsOne()

        entity shouldNotBe null
        entity.name shouldBe show.name
        entity.overview shouldBe getShow().overview
        entity.ratings shouldBe show.ratings
        entity.backdrop_path shouldBe show.backdrop_path
    }

    private fun Tvshow.insertTvShowQuery() {
        tvShowQueries.upsert(
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
