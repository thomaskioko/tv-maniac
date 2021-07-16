package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.tvShowsEntity
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val tvShowQueries get() = database.tvShowQueries

    @Test
    fun insertTvShow() {

        tvShowsEntity.insertTvShowQuery()

        val entities = tvShowQueries.selectAll().executeAsList()

        entities.size shouldBe 1
    }

    @Test
    fun verify_selectByShowId_returnTvShowEntity_afterInsertHasBeenDone() {

        tvShowsEntity.insertTvShowQuery()

        val entity = tvShowQueries.selectByShowId(tvShowsEntity.showId.toLong())
            .executeAsOneOrNull()

        entity shouldNotBe null
        entity?.title shouldBe tvShowsEntity.title
        entity?.description shouldBe tvShowsEntity.description
        entity?.image_url shouldBe tvShowsEntity.imageUrl
        entity?.votes shouldBe tvShowsEntity.votes
        entity?.vote_average shouldBe tvShowsEntity.averageVotes
        entity?.genre_ids shouldBe tvShowsEntity.genreIds
        entity?.show_category shouldBe tvShowsEntity.showCategory
    }

    @Test
    fun verifyDelete_clearsTable() {

        tvShowsEntity.insertTvShowQuery()

        tvShowQueries.deleteAll()

        val entity = tvShowQueries.selectByShowId(tvShowsEntity.showId.toLong())
            .executeAsOneOrNull()

        entity shouldBe null
    }

    private fun TvShowsEntity.insertTvShowQuery() {
        tvShowQueries.insertOrReplace(
            show_id = showId.toLong(),
            title = title,
            description = description,
            language = language,
            image_url = imageUrl,
            votes = votes.toLong(),
            vote_average = averageVotes,
            genre_ids = genreIds,
            show_category = showCategory
        )
    }

}