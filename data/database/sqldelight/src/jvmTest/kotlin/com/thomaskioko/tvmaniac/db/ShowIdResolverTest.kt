package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.Test

class ShowIdResolverTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        TvManiacDatabase.Schema.create(this)
        execute(null, "PRAGMA foreign_keys=ON", 0)
    }
    private val database = DatabaseFactory(driver).createDatabase()
    private val resolver = DefaultShowIdResolver(database)

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `should resolve a tmdbId to the internal showId`() {
        val showId = database.insertShow(traktId = 100, tmdbId = 200)

        resolver.showIdForTmdbId(200) shouldBe showId
    }

    @Test
    fun `should return null for an unknown tmdbId`() {
        database.insertShow(traktId = 100, tmdbId = 200)

        resolver.showIdForTmdbId(999).shouldBeNull()
    }

    @Test
    fun `should resolve a tmdb-only show with no trakt external id row`() {
        database.insertShow(traktId = 0, tmdbId = 300)
        val showId = database.tvShowQueries.getShowIdByTmdbId(Id(300L)).executeAsOne()

        resolver.showIdForTmdbId(300) shouldBe showId
    }

    @Test
    fun `should resolve a traktId to the internal showId`() {
        val showId = database.insertShowWithTrakt(traktId = 500, tmdbId = 600)

        resolver.showIdForTraktId(500) shouldBe showId
    }

    @Test
    fun `should return null for an unknown traktId`() {
        database.insertShowWithTrakt(traktId = 500, tmdbId = 600)

        resolver.showIdForTraktId(999).shouldBeNull()
    }

    private fun TvManiacDatabase.insertShow(traktId: Long, tmdbId: Long): Id<ShowId> {
        tvShowQueries.upsert(
            tmdb_id = Id(tmdbId),
            name = "show-$traktId",
            overview = "overview",
            language = null,
            year = null,
            ratings = 0.0,
            vote_count = 0,
            genres = null,
            status = null,
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return tvShowQueries.getShowIdByTmdbId(Id(tmdbId)).executeAsOne()
    }

    private fun TvManiacDatabase.insertShowWithTrakt(traktId: Long, tmdbId: Long): Id<ShowId> {
        val showId = insertShow(traktId = traktId, tmdbId = tmdbId)
        tvshowExternalIdQueries.insert(
            showId = showId,
            provider = Provider.TRAKT,
            externalId = traktId.toString(),
        )
        return showId
    }
}
