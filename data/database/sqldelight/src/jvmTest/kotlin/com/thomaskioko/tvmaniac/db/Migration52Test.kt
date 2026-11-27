package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.insertTvshow
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration52Test {

    @Test
    fun `should create the rewatch_restore table given migrating from 52 to 53`() {
        openSnapshot(version = 52).use { driver ->
            migrateToVersion(driver, oldVersion = 52, newVersion = 53)

            driver.tableNames() shouldContain "rewatch_restore"
            driver.columnNames("rewatch_restore") shouldContainAll setOf(
                "show_id",
                "season_number",
                "episode_number",
                "session_id",
                "watched_at",
                "ordinal",
            )
        }
    }

    @Test
    fun `should keep an existing viewing given migrating from 52 to 53`() {
        openSnapshot(version = 52).use { driver ->
            driver.addEpisode(showId = 1L, seasonId = 10L, episodeId = 100L, episodeNumber = 1L)
            driver.addViewing(sessionId = 1L, showId = 1L, episodeId = 100L)

            migrateToVersion(driver, oldVersion = 52, newVersion = 53)

            driver.viewingCount(episodeId = 100L) shouldBe 1L
        }
    }

    @Test
    fun `should write the stored viewings to a new episode given migrating from 52 to 53`() {
        openSnapshot(version = 52).use { driver ->
            driver.addEpisode(showId = 1L, seasonId = 10L, episodeId = 100L, episodeNumber = 1L)
            driver.addSession(sessionId = 1L, showId = 1L)
            migrateToVersion(driver, oldVersion = 52, newVersion = 53)
            driver.addRestore(showId = 1L, sessionId = 1L, episodeNumber = 2L, ordinal = 1L)
            driver.addRestore(showId = 1L, sessionId = 1L, episodeNumber = 2L, ordinal = 2L)
            driver.addRestore(showId = 1L, sessionId = 1L, episodeNumber = 2L, ordinal = 3L)

            driver.addEpisodeRow(seasonId = 10L, showId = 1L, episodeId = 101L, episodeNumber = 2L)

            driver.viewingCount(episodeId = 101L) shouldBe 3L
        }
    }

    @Test
    fun `should leave the viewings unchanged given the same episode is stored again after migrating from 52 to 53`() {
        openSnapshot(version = 52).use { driver ->
            driver.addEpisode(showId = 1L, seasonId = 10L, episodeId = 100L, episodeNumber = 1L)
            driver.addSession(sessionId = 1L, showId = 1L)
            migrateToVersion(driver, oldVersion = 52, newVersion = 53)
            driver.addRestore(showId = 1L, sessionId = 1L, episodeNumber = 2L, ordinal = 1L)
            driver.addEpisodeRow(seasonId = 10L, showId = 1L, episodeId = 101L, episodeNumber = 2L)

            driver.upsertEpisodeRow(seasonId = 10L, showId = 1L, episodeId = 101L, episodeNumber = 2L)

            driver.viewingCount(episodeId = 101L) shouldBe 1L
        }
    }

    @Test
    fun `should mark a restored viewing as sent given migrating from 52 to 53`() {
        openSnapshot(version = 52).use { driver ->
            driver.addEpisode(showId = 1L, seasonId = 10L, episodeId = 100L, episodeNumber = 1L)
            driver.addSession(sessionId = 1L, showId = 1L)
            migrateToVersion(driver, oldVersion = 52, newVersion = 53)
            driver.addRestore(showId = 1L, sessionId = 1L, episodeNumber = 2L, ordinal = 1L)

            driver.addEpisodeRow(seasonId = 10L, showId = 1L, episodeId = 101L, episodeNumber = 2L)

            driver.unsentViewingCount(episodeId = 101L) shouldBe 0L
        }
    }
}

private fun SqlDriver.addEpisode(showId: Long, seasonId: Long, episodeId: Long, episodeNumber: Long) {
    insertTvshow(traktId = showId, tmdbId = showId)
    execute(
        identifier = null,
        sql = """
            INSERT INTO season (id, show_id, season_number, title, episode_count)
            VALUES ($seasonId, $showId, 1, 'Season 1', 10)
        """.trimIndent(),
        parameters = 0,
    )
    addEpisodeRow(seasonId = seasonId, showId = showId, episodeId = episodeId, episodeNumber = episodeNumber)
}

private fun SqlDriver.addEpisodeRow(seasonId: Long, showId: Long, episodeId: Long, episodeNumber: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO episode (id, season_id, show_id, episode_number, title, overview, vote_count, ratings)
            VALUES ($episodeId, $seasonId, $showId, $episodeNumber, 'Episode $episodeNumber', 'overview', 0, 0.0)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.upsertEpisodeRow(seasonId: Long, showId: Long, episodeId: Long, episodeNumber: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO episode (id, season_id, show_id, episode_number, title, overview, vote_count, ratings)
            VALUES ($episodeId, $seasonId, $showId, $episodeNumber, 'Episode $episodeNumber', 'overview', 0, 0.0)
            ON CONFLICT(id) DO UPDATE SET title = excluded.title
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.addSession(sessionId: Long, showId: Long) {
    execute(
        identifier = null,
        sql = "INSERT INTO rewatch_session (id, show_id, started_at) VALUES ($sessionId, $showId, 1700000000000)",
        parameters = 0,
    )
}

private fun SqlDriver.addViewing(sessionId: Long, showId: Long, episodeId: Long) {
    addSession(sessionId = sessionId, showId = showId)
    execute(
        identifier = null,
        sql = "INSERT INTO rewatch_session_episode (session_id, episode_id, watched_at) VALUES ($sessionId, $episodeId, 1700000000000)",
        parameters = 0,
    )
}

private fun SqlDriver.addRestore(showId: Long, sessionId: Long, episodeNumber: Long, ordinal: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO rewatch_restore (show_id, season_number, episode_number, session_id, watched_at, ordinal)
            VALUES ($showId, 1, $episodeNumber, $sessionId, 1700000000000, $ordinal)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.viewingCount(episodeId: Long): Long = count("SELECT COUNT(*) FROM rewatch_session_episode WHERE episode_id = $episodeId")

private fun SqlDriver.unsentViewingCount(episodeId: Long): Long =
    count("SELECT COUNT(*) FROM rewatch_session_episode WHERE episode_id = $episodeId AND synced_at IS NULL")

private fun SqlDriver.count(sql: String): Long = executeQuery(
    identifier = null,
    sql = sql,
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value
