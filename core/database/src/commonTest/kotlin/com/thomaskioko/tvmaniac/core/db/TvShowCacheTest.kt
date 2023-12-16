package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getShow
import com.thomaskioko.tvmaniac.core.db.MockData.showList
import com.thomaskioko.tvmaniac.db.Id
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val trendingShowsQueries get() = database.trending_showsQueries
    private val tvShowQueries get() = database.tvshowsQueries

    @Test
    fun insertTvShow() {
        val shows = showList()

        shows.insertTvShowsEntityList()

        for (show in shows) {
            Trending_shows(
                id = show.id,
                page = Id(1),
            ).insert()
        }

        val entities = trendingShowsQueries.trendingShows().executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun verify_selectByShowId_returnTvShowEntity_afterInsertHasBeenDone() {
        val show = getShow()
        show.insertTvShowQuery()

        val entity = tvShowQueries.tvshowDetails(show.id)
            .executeAsOne()

        entity shouldNotBe null
        entity.name shouldBe show.name
        entity.overview shouldBe getShow().overview
        entity.vote_average shouldBe show.vote_average
        entity.backdrop_path shouldBe show.backdrop_path
        entity.popularity shouldBe show.popularity
    }

    private fun List<Tvshows>.insertTvShowsEntityList() {
        map { it.insertTvShowQuery() }
    }

    private fun Tvshows.insertTvShowQuery() {
        tvShowQueries.upsert(
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
        trendingShowsQueries.insert(
            id = id,
            page = page,
        )
    }
}
