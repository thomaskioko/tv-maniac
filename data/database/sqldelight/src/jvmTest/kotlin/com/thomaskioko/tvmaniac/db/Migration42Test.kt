package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.enableForeignKeys
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration42Test {

    @Test
    fun `should create show_ratings table given migration to version 42`() {
        openSnapshot(version = 41).use { driver ->
            migrateToVersion(driver, oldVersion = 41, newVersion = 42)

            val cols = driver.columnNames("show_ratings")
            cols shouldContain "show_id"
            cols shouldContain "user_rating"
            cols shouldContain "rated_at"
            cols shouldContain "pending_action"
        }
    }

    @Test
    fun `should create season_ratings table given migration to version 42`() {
        openSnapshot(version = 41).use { driver ->
            migrateToVersion(driver, oldVersion = 41, newVersion = 42)

            val cols = driver.columnNames("season_ratings")
            cols shouldContain "season_id"
            cols shouldContain "user_rating"
            cols shouldContain "rated_at"
            cols shouldContain "pending_action"
        }
    }

    @Test
    fun `should create episode_ratings table given migration to version 42`() {
        openSnapshot(version = 41).use { driver ->
            migrateToVersion(driver, oldVersion = 41, newVersion = 42)

            val cols = driver.columnNames("episode_ratings")
            cols shouldContain "episode_id"
            cols shouldContain "user_rating"
            cols shouldContain "rated_at"
            cols shouldContain "pending_action"
        }
    }

    @Test
    fun `should default pending_action to NOTHING given a new show rating row`() {
        openSnapshot(version = 41).use { driver ->
            migrateToVersion(driver, oldVersion = 41, newVersion = 42)
            driver.seedShow(showId = 1L)

            driver.execute(
                identifier = null,
                sql = "INSERT INTO show_ratings (show_id, user_rating) VALUES (1, 8)",
                parameters = 0,
            )

            driver.showRatingPendingAction(1L) shouldBe "NOTHING"
        }
    }

    @Test
    fun `should cascade delete show rating given parent show is deleted`() {
        openSnapshot(version = 41).use { driver ->
            migrateToVersion(driver, oldVersion = 41, newVersion = 42)
            driver.enableForeignKeys()
            driver.seedShow(showId = 1L)
            driver.execute(
                identifier = null,
                sql = "INSERT INTO show_ratings (show_id, user_rating) VALUES (1, 8)",
                parameters = 0,
            )

            driver.execute(identifier = null, sql = "DELETE FROM tvshow WHERE id = 1", parameters = 0)

            driver.showRatingsCount() shouldBe 0
        }
    }

    @Test
    fun `should reject pending_action value outside allowed set`() {
        openSnapshot(version = 41).use { driver ->
            migrateToVersion(driver, oldVersion = 41, newVersion = 42)
            driver.seedShow(showId = 1L)

            val result = runCatching {
                driver.execute(
                    identifier = null,
                    sql = "INSERT INTO show_ratings (show_id, pending_action) VALUES (1, 'INVALID')",
                    parameters = 0,
                )
            }

            result.isFailure shouldBe true
        }
    }
}

private fun SqlDriver.seedShow(showId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (id, tmdb_id, name, overview, ratings, vote_count)
            VALUES ($showId, $showId, 'Show $showId', '', 0.0, 0)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.showRatingPendingAction(showId: Long): String? = executeQuery(
    identifier = null,
    sql = "SELECT pending_action FROM show_ratings WHERE show_id = $showId",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        var value: String? = null
        if (cursor.next().value) {
            value = cursor.getString(0)
        }
        QueryResult.Value(value)
    },
).value

private fun SqlDriver.showRatingsCount(): Long = executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM show_ratings",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value
