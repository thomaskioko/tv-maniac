package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.Test

class SchemaCreateTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        TvManiacDatabase.Schema.create(this)
        execute(null, "PRAGMA foreign_keys=ON", 0)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `should provision all expected tables at current version`() {
        driver.tableNames() shouldContainAll setOf(
            "tvshow",
            "season",
            "episode",
            "followed_shows",
            "watched_episodes",
            "trakt_lists",
            "trakt_list_shows",
            "calendar_entry",
            "show_metadata",
            "user",
        )
    }

    @Test
    fun `should expose generated queries against fresh schema`() {
        val database = DatabaseFactory(driver).createDatabase()

        database.followedShowsQueries.entries().executeAsList() shouldBe emptyList()
        database.traktListsQueries.selectAll().executeAsList() shouldBe emptyList()
        database.traktListShowsQueries.countActiveByListId().executeAsList() shouldBe emptyList()
    }
}
