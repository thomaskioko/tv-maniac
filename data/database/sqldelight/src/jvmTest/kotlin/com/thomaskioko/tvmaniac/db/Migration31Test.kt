package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration31Test {

    @Test
    fun `should add title and year columns to continue_watching`() {
        openSnapshot(version = 31).use { driver ->
            migrateToCurrent(driver, oldVersion = 31)

            driver.columnNames("continue_watching") shouldContainAll setOf("title", "year")
        }
    }

    @Test
    fun `should preserve existing rows when adding title and year`() {
        openSnapshot(version = 31).use { driver ->
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO trakt_continue_watching
                        (trakt_id, tmdb_id, aired_episodes, completed_count, last_watched_at, last_updated_at)
                    VALUES (42, 4242, 10, 5, 1700000000000, 1700000000000)
                """.trimIndent(),
                parameters = 0,
            )

            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
                    VALUES (42, 4242, 'show-42', 'overview', 0.0, 0)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToCurrent(driver, oldVersion = 31)

            val title = driver.executeQuery(
                identifier = null,
                sql = """
                    SELECT cw.title
                    FROM continue_watching cw
                    JOIN show_trakt ON show_trakt.show_id = cw.show_id
                    WHERE show_trakt.trakt_id = 42
                """.trimIndent(),
                parameters = 0,
                binders = null,
                mapper = { cursor ->
                    cursor.next()
                    QueryResult.Value(cursor.getString(0))
                },
            ).value
            title shouldBe null
        }
    }

    @Test
    fun `should accept title and year on upsert after migration`() {
        openSnapshot(version = 31).use { driver ->
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO trakt_continue_watching
                        (trakt_id, tmdb_id, aired_episodes, completed_count, last_watched_at, last_updated_at)
                    VALUES (7, 77, 12, 6, 1700000000000, 1700000000000)
                """.trimIndent(),
                parameters = 0,
            )

            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
                    VALUES (7, 77, 'Severance', 'overview', 0.0, 0)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToCurrent(driver, oldVersion = 31)

            driver.execute(
                identifier = null,
                sql = """
                    UPDATE continue_watching
                    SET title = 'Severance', year = 2022
                    WHERE show_id = (SELECT show_id FROM show_trakt WHERE trakt_id = 7)
                """.trimIndent(),
                parameters = 0,
            )

            val title = driver.executeQuery(
                identifier = null,
                sql = """
                    SELECT cw.title
                    FROM continue_watching cw
                    JOIN show_trakt ON show_trakt.show_id = cw.show_id
                    WHERE show_trakt.trakt_id = 7
                """.trimIndent(),
                parameters = 0,
                binders = null,
                mapper = { cursor ->
                    cursor.next()
                    QueryResult.Value(cursor.getString(0))
                },
            ).value
            title shouldBe "Severance"

            val year = driver.executeQuery(
                identifier = null,
                sql = """
                    SELECT cw.year
                    FROM continue_watching cw
                    JOIN show_trakt ON show_trakt.show_id = cw.show_id
                    WHERE show_trakt.trakt_id = 7
                """.trimIndent(),
                parameters = 0,
                binders = null,
                mapper = { cursor ->
                    cursor.next()
                    QueryResult.Value(cursor.getLong(0))
                },
            ).value
            year shouldBe 2022L
        }
    }
}
