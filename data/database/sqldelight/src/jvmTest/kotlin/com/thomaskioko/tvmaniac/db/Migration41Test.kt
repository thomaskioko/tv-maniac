package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.notNullColumns
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration41Test {

    @Test
    fun `should make trakt_id nullable in calendar_entry`() {
        openSnapshot(version = 40).use { driver ->
            migrateToVersion(driver, oldVersion = 40, newVersion = 41)

            val cols = driver.columnNames("calendar_entry")
            cols shouldContain "trakt_id"
            driver.notNullColumns("calendar_entry") shouldNotContain "trakt_id"
        }
    }

    @Test
    fun `should preserve existing rows given calendar entries with non-zero trakt_id`() {
        openSnapshot(version = 40).use { driver ->
            driver.seedCalendarEntry(
                showTitle = "Breaking Bad",
                episodeTraktId = 73640L,
                seasonNumber = 1,
                episodeNumber = 1,
            )

            migrateToVersion(driver, oldVersion = 40, newVersion = 41)

            val rows = driver.calendarRows()
            rows.size shouldBe 1
            rows[0].traktId shouldBe 73640L
            rows[0].showTitle shouldBe "Breaking Bad"
        }
    }

    @Test
    fun `should convert zero sentinel to null given calendar entries with trakt_id zero`() {
        openSnapshot(version = 40).use { driver ->
            driver.seedCalendarEntry(
                showTitle = "Simkl Show",
                episodeTraktId = 0L,
                seasonNumber = 1,
                episodeNumber = 1,
            )

            migrateToVersion(driver, oldVersion = 40, newVersion = 41)

            val rows = driver.calendarRows()
            rows.size shouldBe 1
            rows[0].traktId shouldBe null
        }
    }

    @Test
    fun `should allow inserting entry with null trakt_id after migration`() {
        openSnapshot(version = 40).use { driver ->
            migrateToVersion(driver, oldVersion = 40, newVersion = 41)

            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO calendar_entry (
                        show_id, trakt_id, season_number, episode_number,
                        air_date, show_title
                    )
                    VALUES (1, NULL, 1, 1, 1700000000, 'Null Trakt Show')
                """.trimIndent(),
                parameters = 0,
            )

            val rows = driver.calendarRows()
            rows.size shouldBe 1
            rows[0].traktId shouldBe null
            rows[0].showTitle shouldBe "Null Trakt Show"
        }
    }
}

private data class CalendarRow(val traktId: Long?, val showTitle: String)

private fun SqlDriver.seedCalendarEntry(
    showTitle: String,
    episodeTraktId: Long,
    seasonNumber: Long,
    episodeNumber: Long,
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO calendar_entry (
                show_id, trakt_id, season_number, episode_number,
                air_date, show_title
            )
            VALUES (1, $episodeTraktId, $seasonNumber, $episodeNumber, 1700000000, '$showTitle')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.calendarRows(): List<CalendarRow> = executeQuery(
    identifier = null,
    sql = "SELECT trakt_id, show_title FROM calendar_entry ORDER BY episode_number",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<CalendarRow>()
        while (cursor.next().value) {
            rows.add(CalendarRow(traktId = cursor.getLong(0), showTitle = cursor.getString(1)!!))
        }
        QueryResult.Value(rows.toList())
    },
).value
