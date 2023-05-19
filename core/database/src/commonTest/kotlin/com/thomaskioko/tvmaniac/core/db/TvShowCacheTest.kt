package com.thomaskioko.tvmaniac.core.db

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

        val entities = tvShowQueries.shows().executeAsList()

        entities.size shouldBe 2
    }

    @Test
    fun verify_selectByShowId_returnTvShowEntity_afterInsertHasBeenDone() {
        getShow().insertTvShowQuery()

        val entity = tvShowQueries.showById(getShow().trakt_id)
            .executeAsOne()

        entity shouldNotBe null
        entity.title shouldBe getShow().title
        entity.overview shouldBe getShow().overview
        entity.votes shouldBe getShow().votes
        entity.votes shouldBe getShow().votes
        entity.genres shouldBe getShow().genres
    }

    @Test
    fun verifyDelete_clearsTable() {
        getShow().insertTvShowQuery()

        tvShowQueries.deleteAll()

        val entity = tvShowQueries.showById(getShow().trakt_id)
            .executeAsOneOrNull()

        entity shouldBe null
    }

    private fun List<Show>.insertTvShowsEntityList() {
        map { it.insertTvShowQuery() }
    }

    private fun Show.insertTvShowQuery() {
        tvShowQueries.insertOrReplace(
            trakt_id = trakt_id,
            title = title,
            overview = overview,
            language = language,
            votes = votes,
            runtime = runtime,
            genres = genres,
            year = year,
            status = status,
            tmdb_id = tmdb_id,
            rating = rating,
        )
    }
}
