package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.makeTvShowList
import com.thomaskioko.tvmaniac.MockData.tvShow
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val tvShowQueries get() = database.tvShowQueries

    @Test
    fun insertTvShow() {

        makeTvShowList().insertTvShowsEntityList()

        val entities = tvShowQueries.selectAll().executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun verify_selectByShowId_returnTvShowEntity_afterInsertHasBeenDone() {

        tvShow.insertTvShowQuery()

        val entity = tvShowQueries.selectByShowId(tvShow.id.toLong())
            .executeAsOne()

        entity shouldNotBe null
        entity.title shouldBe tvShow.title
        entity.description shouldBe tvShow.overview
        entity.poster_image_url shouldBe tvShow.posterImageUrl
        entity.backdrop_image_url shouldBe tvShow.backdropImageUrl
        entity.votes shouldBe tvShow.votes
        entity.vote_average shouldBe tvShow.averageVotes
        entity.genre_ids shouldBe tvShow.genreIds
        entity.show_category shouldBe tvShow.showCategory
    }

    @Test
    fun givenTvShowHasSeasons_queryReturnsCorrectData() {

        makeTvShowList().insertTvShowsEntityList()

        val seasons = tvShowQueries.selectByShowId(84958)
            .executeAsOne().season_ids

        //Verify that the first time the list is empty
        seasons shouldBe null

        tvShowQueries.updateTvShow(
            id = 84958,
            season_ids = listOf(114355, 77680),
            status = "Returning  Series"
        )

        val seasonIds = tvShowQueries.selectByShowId(tvShow.id.toLong())
            .executeAsOne().season_ids

        //Verify that the list has been updated and exists
        seasonIds shouldBe listOf(114355, 77680)

    }

    @Test
    fun givenTvShowIsUpdated_verifyDataIs_InsertedCorrectly() {

        makeTvShowList().insertTvShowsEntityList()

        val entity = tvShowQueries.selectByShowId(tvShow.id.toLong())
            .executeAsOne()

        entity.season_ids shouldBe null

        tvShowQueries.updateTvShow(
            id = tvShow.id.toLong(),
            season_ids = listOf(2534997, 2927202),
            status = "Returning  Series"
        )

        val seasonsIds = tvShowQueries.selectByShowId(tvShow.id.toLong())
            .executeAsOne().season_ids

        seasonsIds shouldBe listOf(2534997, 2927202)
    }

    @Test
    fun verifyDelete_clearsTable() {

        tvShow.insertTvShowQuery()

        tvShowQueries.deleteAll()

        val entity = tvShowQueries.selectByShowId(tvShow.id.toLong())
            .executeAsOneOrNull()

        entity shouldBe null
    }

    private fun List<TvShow>.insertTvShowsEntityList() {
        map { it.insertTvShowQuery() }
    }

    private fun TvShow.insertTvShowQuery() {
        tvShowQueries.insertOrReplace(
            id = id.toLong(),
            title = title,
            description = overview,
            language = language,
            poster_image_url = posterImageUrl,
            backdrop_image_url = backdropImageUrl,
            votes = votes.toLong(),
            vote_average = averageVotes,
            genre_ids = genreIds,
            show_category = showCategory,
            time_window = timeWindow,
            year = year,
            status = status,
            popularity = 0.0
        )
    }

}