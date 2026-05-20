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
    fun `should add title and year columns to trakt_continue_watching`() {
        openSnapshot(version = 31).use { driver ->
            migrateToCurrent(driver, oldVersion = 31)

            driver.columnNames("trakt_continue_watching") shouldContainAll setOf("title", "year")
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

            migrateToCurrent(driver, oldVersion = 31)

            val title = driver.executeQuery(
                identifier = null,
                sql = "SELECT title FROM trakt_continue_watching WHERE trakt_id = 42",
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
            migrateToCurrent(driver, oldVersion = 31)

            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO trakt_continue_watching
                        (trakt_id, tmdb_id, aired_episodes, completed_count, last_watched_at, last_updated_at, title, year)
                    VALUES (7, 77, 12, 6, 1700000000000, 1700000000000, 'Severance', 2022)
                """.trimIndent(),
                parameters = 0,
            )

            val title = driver.executeQuery(
                identifier = null,
                sql = "SELECT title FROM trakt_continue_watching WHERE trakt_id = 7",
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
                sql = "SELECT year FROM trakt_continue_watching WHERE trakt_id = 7",
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
