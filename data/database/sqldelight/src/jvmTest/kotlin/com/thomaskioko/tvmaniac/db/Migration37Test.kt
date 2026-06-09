package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.insertEpisode
import com.thomaskioko.tvmaniac.db.util.insertSeason
import com.thomaskioko.tvmaniac.db.util.insertTvshow
import com.thomaskioko.tvmaniac.db.util.insertWatchedEpisode
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration37Test {

    @Test
    fun `should drop episode trakt_id column`() {
        openSnapshot(version = 37).use { driver ->
            migrateToVersion(driver, oldVersion = 37, newVersion = 38)

            driver.columnNames("episode") shouldNotContain "trakt_id"
        }
    }

    @Test
    fun `should clear episode and season while preserving watched_episodes with episode_id nulled`() {
        openSnapshot(version = 37).use { driver ->
            driver.insertTvshow(traktId = 100, tmdbId = 200)
            driver.insertSeason(id = 1, showTraktId = 100, seasonNumber = 1)
            driver.insertEpisode(
                id = 100,
                seasonId = 1,
                showTraktId = 100,
                episodeNumber = 1,
                firstAired = 1_700_000_000_000L,
            )
            driver.insertWatchedEpisode(
                showTraktId = 100,
                episodeId = 100,
                seasonNumber = 1,
                episodeNumber = 1,
                pendingAction = "UPLOAD",
            )
            driver.execute(
                identifier = null,
                sql = "INSERT INTO last_requests (entity_id, request_type, timestamp) VALUES (100, 'SHOW_DETAILS', 1700000000000)",
                parameters = 0,
            )

            migrateToVersion(driver, oldVersion = 37, newVersion = 38)

            driver.rowCount("episode") shouldBe 0L
            driver.rowCount("season") shouldBe 0L
            driver.rowCount("last_requests") shouldBe 0L

            val watched = driver.watchedRows()
            watched.size shouldBe 1
            watched[0].episodeId shouldBe null
            watched[0].seasonNumber shouldBe 1L
            watched[0].episodeNumber shouldBe 1L
            watched[0].pendingAction shouldBe "UPLOAD"
        }
    }
}

private fun SqlDriver.rowCount(table: String): Long = executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM $table",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value

private data class WatchedRow(
    val episodeId: Long?,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val pendingAction: String,
)

private fun SqlDriver.watchedRows(): List<WatchedRow> = executeQuery(
    identifier = null,
    sql = """
        SELECT episode_id, season_number, episode_number, pending_action
        FROM watched_episodes
        ORDER BY season_number, episode_number
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<WatchedRow>()
        while (cursor.next().value) {
            rows.add(
                WatchedRow(
                    episodeId = cursor.getLong(0),
                    seasonNumber = cursor.getLong(1)!!,
                    episodeNumber = cursor.getLong(2)!!,
                    pendingAction = cursor.getString(3)!!,
                ),
            )
        }
        QueryResult.Value(rows.toList())
    },
).value
