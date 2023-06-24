package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getShow
import com.thomaskioko.tvmaniac.core.db.MockData.showCategory
import com.thomaskioko.tvmaniac.core.db.MockData.showList
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TvShowCacheTest : BaseDatabaseTest() {

    private val tvShowQueries get() = database.showQueries
    private val showCategoryQueries get() = database.show_categoryQueries

    @Test
    fun insertTvShow() {
        val shows = showList()

        shows.insertTvShowsEntityList()

        for (show in shows) {
            showCategory(show.trakt_id, 1).insertCategory()
        }

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

    private fun Show_category.insertCategory() {
        showCategoryQueries.insertOrReplace(
            trakt_id = trakt_id,
            category_id = category_id,
        )
    }
}
