package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getShow
import com.thomaskioko.tvmaniac.core.db.MockData.makeShowList
import com.thomaskioko.tvmaniac.datasource.cache.Show
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

        val status = tvShowQueries.selectByShowId(84958)
            .executeAsOne().status

        // Verify that the first time the list is empty
        status shouldBe null

        tvShowQueries.updateTvShow(
            id = 84958,
            status = "Returning  Series",
            number_of_episodes = 12,
            number_of_seasons = 2
        )

        val updatedStatus = tvShowQueries.selectByShowId(84958)
            .executeAsOne().status

        // Verify that the list has been updated and exists
        updatedStatus shouldBe "Returning  Series"
    }

    @Test
    fun givenTvShowIsAddedToWatchList_verifyDataIs_InsertedCorrectly() {

        makeShowList().insertTvShowsEntityList()

        tvShowQueries.updateFollowinglist(
            id = 84958.toLong(),
            following = true
        )

        val watchlist = tvShowQueries.selectFollowinglist().executeAsList()

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
            following = false
        )
    }
}
