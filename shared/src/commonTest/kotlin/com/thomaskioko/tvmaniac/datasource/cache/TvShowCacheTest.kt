package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.makeTvShowEntityList
import com.thomaskioko.tvmaniac.MockData.tvSeasonsList
import com.thomaskioko.tvmaniac.MockData.tvShowsEntity
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val tvShowQueries get() = database.tvShowQueries

    @Test
    fun insertTvShow() {

        makeTvShowEntityList().insertTvShowsEntityList()

        val entities = tvShowQueries.selectAll().executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun verify_selectByShowId_returnTvShowEntity_afterInsertHasBeenDone() {

        tvShowsEntity.insertTvShowQuery()

        val entity = tvShowQueries.selectByShowId(tvShowsEntity.id.toLong())
            .executeAsOne()

        entity shouldNotBe null
        entity.title shouldBe tvShowsEntity.title
        entity.description shouldBe tvShowsEntity.description
        entity.image_url shouldBe tvShowsEntity.imageUrl
        entity.votes shouldBe tvShowsEntity.votes
        entity.vote_average shouldBe tvShowsEntity.averageVotes
        entity.genre_ids shouldBe tvShowsEntity.genreIds
        entity.show_category shouldBe tvShowsEntity.showCategory
    }

    @Test
    fun givenTvShowHasSeasons_queryReturnsCorrectData() {

        makeTvShowEntityList().insertTvShowsEntityList()

        val seasons = tvShowQueries.selectByShowId(tvShowsEntity.id.toLong())
            .executeAsOne().seasons

        //Verify that the first time the list is empty
        seasons shouldBe null

        tvShowQueries.updateTvShow(
            id = tvShowsEntity.id.toLong(),
            seasons = tvSeasonsList
        )

        val tvSeasonsResult = tvShowQueries.selectByShowId(tvShowsEntity.id.toLong())
            .executeAsOne().seasons

        //Verify that the list has been updated and exists
        tvSeasonsResult shouldBe tvSeasonsList

    }

    @Test
    fun givenTvShowIsUpdated_verifyDataIs_InsertedCorrectly() {

        makeTvShowEntityList().insertTvShowsEntityList()

        val entity = tvShowQueries.selectByShowId(tvShowsEntity.id.toLong())
            .executeAsOne()

        entity.seasons shouldBe null

        tvShowQueries.updateTvShow(
            id = tvShowsEntity.id.toLong(),
            seasons = tvSeasonsList
        )

        val tvShowResult = tvShowQueries.selectByShowId(tvShowsEntity.id.toLong())
            .executeAsOne()

        tvShowResult.seasons shouldBe tvSeasonsList
    }

    @Test
    fun verifyDelete_clearsTable() {

        tvShowsEntity.insertTvShowQuery()

        tvShowQueries.deleteAll()

        val entity = tvShowQueries.selectByShowId(tvShowsEntity.id.toLong())
            .executeAsOneOrNull()

        entity shouldBe null
    }

    private fun List<TvShowsEntity>.insertTvShowsEntityList() {
        map { it.insertTvShowQuery() }
    }

    private fun TvShowsEntity.insertTvShowQuery() {
        tvShowQueries.insertOrReplace(
            id = id.toLong(),
            title = title,
            description = description,
            language = language,
            image_url = imageUrl,
            votes = votes.toLong(),
            vote_average = averageVotes,
            genre_ids = genreIds,
            show_category = showCategory,
            time_window = timeWindow
        )
    }

}