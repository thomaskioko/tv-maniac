package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration39Test {

    @Test
    fun `should rename similar_show_trakt_id to trakt_id in similar_shows`() {
        openSnapshot(version = 38).use { driver ->
            migrateToVersion(driver, oldVersion = 38, newVersion = 39)

            val cols = driver.columnNames("similar_shows")
            cols shouldContain "trakt_id"
            cols shouldNotContain "similar_show_trakt_id"
        }
    }

    @Test
    fun `should rename recommended_show_trakt_id to trakt_id in recommended_shows`() {
        openSnapshot(version = 38).use { driver ->
            migrateToVersion(driver, oldVersion = 38, newVersion = 39)

            val cols = driver.columnNames("recommended_shows")
            cols shouldContain "trakt_id"
            cols shouldNotContain "recommended_show_trakt_id"
        }
    }

    @Test
    fun `should rename show_trakt_id to trakt_id in trakt_list_shows`() {
        openSnapshot(version = 38).use { driver ->
            migrateToVersion(driver, oldVersion = 38, newVersion = 39)

            val cols = driver.columnNames("trakt_list_shows")
            cols shouldContain "trakt_id"
            cols shouldNotContain "show_trakt_id"
        }
    }

    @Test
    fun `should rename show_trakt_id to show_id and episode_trakt_id to trakt_id in calendar_entry`() {
        openSnapshot(version = 38).use { driver ->
            migrateToVersion(driver, oldVersion = 38, newVersion = 39)

            val cols = driver.columnNames("calendar_entry")
            cols shouldContain "show_id"
            cols shouldContain "trakt_id"
            cols shouldNotContain "show_trakt_id"
            cols shouldNotContain "episode_trakt_id"
        }
    }

    @Test
    fun `should convert show_trakt_id to internal show_id when mapping exists`() {
        openSnapshot(version = 38).use { driver ->
            driver.seedTvshow(internalId = 1L, tmdbId = 200L)
            driver.seedExternalId(internalId = 1L, traktId = 100L)
            driver.seedCalendarEntry(showTraktId = 100L, episodeTraktId = 500L)

            migrateToVersion(driver, oldVersion = 38, newVersion = 39)

            driver.calendarShowIds() shouldBe listOf(1L)
        }
    }

    @Test
    fun `should delete calendar_entry when no mapping exists for show_trakt_id`() {
        openSnapshot(version = 38).use { driver ->
            driver.seedCalendarEntry(showTraktId = 999L, episodeTraktId = 501L)

            migrateToVersion(driver, oldVersion = 38, newVersion = 39)

            driver.calendarRowCount() shouldBe 0L
        }
    }
}

private fun SqlDriver.seedTvshow(internalId: Long, tmdbId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (id, tmdb_id, name, overview, ratings, vote_count)
            VALUES ($internalId, $tmdbId, 'show-$internalId', 'overview', 7.5, 100)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.seedExternalId(internalId: Long, traktId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT OR IGNORE INTO tvshow_external_id (show_id, provider, external_id)
            VALUES ($internalId, 'TRAKT', '$traktId')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.seedCalendarEntry(showTraktId: Long, episodeTraktId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO calendar_entry (
                show_trakt_id, episode_trakt_id,
                season_number, episode_number,
                air_date, show_title
            )
            VALUES ($showTraktId, $episodeTraktId, 1, 1, 1700000000, 'show-$showTraktId')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.calendarShowIds(): List<Long> = executeQuery(
    identifier = null,
    sql = "SELECT show_id FROM calendar_entry ORDER BY show_id",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val ids = mutableListOf<Long>()
        while (cursor.next().value) {
            cursor.getLong(0)?.let(ids::add)
        }
        QueryResult.Value(ids.toList())
    },
).value

private fun SqlDriver.calendarRowCount(): Long = executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM calendar_entry",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value
