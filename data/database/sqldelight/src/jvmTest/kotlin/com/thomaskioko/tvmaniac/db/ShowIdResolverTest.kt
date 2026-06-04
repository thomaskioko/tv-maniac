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
    fun `should resolve a traktId to the internal showId via its TRAKT external id`() {
        val showId = database.insertShowWithTraktExternalId(traktId = 100, tmdbId = 200)

        resolver.showIdForTraktId(100) shouldBe showId
    }

    @Test
    fun `should return null for a traktId with no external id row`() {
        database.insertShowWithTraktExternalId(traktId = 100, tmdbId = 200)

        resolver.showIdForTraktId(999).shouldBeNull()
    }

    @Test
    fun `should keep a trakt id mapped to its first show when another show claims the same id`() {
        val showA = database.insertShowWithTraktExternalId(traktId = 100, tmdbId = 200)
        val showB = database.insertShow(traktId = 101, tmdbId = 201)

        // The UNIQUE(provider, external_id) guard makes this a no-op rather than a mis-merge.
        database.tvshowExternalIdQueries.insert(showId = showB, provider = Provider.TRAKT, externalId = "100")

        resolver.showIdForTraktId(100) shouldBe showA
    }

    private fun TvManiacDatabase.insertShow(traktId: Long, tmdbId: Long): Id<ShowId> {
        tvShowQueries.upsert(
            trakt_id = Id(traktId),
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
        return tvShowQueries.tvshowByTraktId(Id(traktId)).executeAsOne().id
    }

    private fun TvManiacDatabase.insertShowWithTraktExternalId(traktId: Long, tmdbId: Long): Id<ShowId> {
        val showId = insertShow(traktId, tmdbId)
        tvshowExternalIdQueries.insert(showId = showId, provider = Provider.TRAKT, externalId = traktId.toString())
        return showId
    }
}
