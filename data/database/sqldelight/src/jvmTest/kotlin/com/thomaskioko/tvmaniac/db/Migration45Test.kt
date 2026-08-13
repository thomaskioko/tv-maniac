package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration45Test {

    @Test
    fun `should clear genres stored in the old format given migration to version 45`() {
        openSnapshot(version = 44).use { driver ->
            driver.insertShow(tmdbId = 1L, genres = "Science-fiction,Drama")
            driver.insertShow(tmdbId = 2L, genres = "Science Fiction,Drama")
            driver.insertShow(tmdbId = 3L, genres = null)

            migrateToVersion(driver, oldVersion = 44, newVersion = 45)

            driver.genresFor(tmdbId = 1L) shouldBe null
            driver.genresFor(tmdbId = 2L) shouldBe "Science Fiction,Drama"
            driver.genresFor(tmdbId = 3L) shouldBe null
        }
    }

    @Test
    fun `should clear request freshness given migration to version 45`() {
        openSnapshot(version = 44).use { driver ->
            driver.execute(
                identifier = null,
                sql = "INSERT INTO last_requests (entity_id, request_type, timestamp) VALUES (1, 'SHOW_DETAILS', 0)",
                parameters = 0,
            )

            migrateToVersion(driver, oldVersion = 44, newVersion = 45)

            driver.countLastRequests() shouldBe 0L
        }
    }

    private fun SqlDriver.insertShow(tmdbId: Long, genres: String?) {
        val genresValue = genres?.let { "'$it'" } ?: "NULL"
        execute(
            identifier = null,
            sql = """
                INSERT INTO tvshow (tmdb_id, name, overview, ratings, vote_count, genres)
                VALUES ($tmdbId, 'show-$tmdbId', 'overview', 0.0, 0, $genresValue)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.genresFor(tmdbId: Long): String? = executeQuery(
        identifier = null,
        sql = "SELECT genres FROM tvshow WHERE tmdb_id = $tmdbId",
        parameters = 0,
        binders = null,
        mapper = { cursor ->
            cursor.next()
            QueryResult.Value(cursor.getString(0))
        },
    ).value

    private fun SqlDriver.countLastRequests(): Long = executeQuery(
        identifier = null,
        sql = "SELECT COUNT(*) FROM last_requests",
        parameters = 0,
        binders = null,
        mapper = { cursor ->
            cursor.next()
            QueryResult.Value(cursor.getLong(0) ?: 0L)
        },
    ).value
}
