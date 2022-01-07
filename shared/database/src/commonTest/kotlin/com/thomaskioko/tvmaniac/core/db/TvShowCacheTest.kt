package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.core.db.MockData.getShow
import com.thomaskioko.tvmaniac.core.db.MockData.makeShowList
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val tvShowQueries get() = database.showQueries

    @Test
    fun insertTvShow() {

        makeShowList().insertTvShowsEntityList()

        val entities = tvShowQueries.selectAll().executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun verify_selectByShowId_returnTvShowEntity_afterInsertHasBeenDone() {

        getShow().insertTvShowQuery()

        val entity = tvShowQueries.selectByShowId(getShow().id)
            .executeAsOne()

        entity shouldNotBe null
        entity.title shouldBe getShow().title
        entity.description shouldBe getShow().description
        entity.poster_image_url shouldBe getShow().poster_image_url
        entity.backdrop_image_url shouldBe getShow().backdrop_image_url
        entity.votes shouldBe getShow().votes
        entity.vote_average shouldBe getShow().vote_average
        entity.genre_ids shouldBe getShow().genre_ids
    }

    @Test
    fun givenTvShowHasSeasons_queryReturnsCorrectData() {

        makeShowList().insertTvShowsEntityList()

        val seasons = tvShowQueries.selectByShowId(84958)
            .executeAsOne().season_ids

        // Verify that the first time the list is empty
        seasons shouldBe null

        tvShowQueries.updateTvShow(
            id = 84958,
            season_ids = listOf(114355, 77680),
            status = "Returning  Series"
        )

        val seasonIds = tvShowQueries.selectByShowId(getShow().id)
            .executeAsOne().season_ids

        // Verify that the list has been updated and exists
        seasonIds shouldBe listOf(114355, 77680)
    }

    @Test
    fun givenTvShowIsUpdated_verifyDataIs_InsertedCorrectly() {

        makeShowList().insertTvShowsEntityList()

        val entity = tvShowQueries.selectByShowId(getShow().id)
            .executeAsOne()

        entity.season_ids shouldBe null

        tvShowQueries.updateTvShow(
            id = getShow().id,
            season_ids = listOf(2534997, 2927202),
            status = "Returning  Series"
        )

        val seasonsIds = tvShowQueries.selectByShowId(getShow().id)
            .executeAsOne().season_ids

        seasonsIds shouldBe listOf(2534997, 2927202)
    }

    @Test
    fun givenTvShowIsAddedToWatchList_verifyDataIs_InsertedCorrectly() {

        makeShowList().insertTvShowsEntityList()

        tvShowQueries.updateWatchlist(
            id = 84958.toLong(),
            is_watchlist = true
        )

        val watchlist = tvShowQueries.selectWatchlist().executeAsList()

        watchlist.size shouldBe 1
    }

    @Test
    fun verifyDelete_clearsTable() {

        getShow().insertTvShowQuery()

        tvShowQueries.deleteAll()

        val entity = tvShowQueries.selectByShowId(getShow().id)
            .executeAsOneOrNull()

        entity shouldBe null
    }

    private fun List<Show>.insertTvShowsEntityList() {
        map { it.insertTvShowQuery() }
    }

    private fun Show.insertTvShowQuery() {
        tvShowQueries.insertOrReplace(
            id = id,
            title = title,
            description = description,
            language = language,
            poster_image_url = poster_image_url,
            backdrop_image_url = backdrop_image_url,
            votes = votes,
            vote_average = vote_average,
            genre_ids = genre_ids,
            year = year,
            status = status,
            popularity = 0.0,
            is_watchlist = false
        )
    }
}
