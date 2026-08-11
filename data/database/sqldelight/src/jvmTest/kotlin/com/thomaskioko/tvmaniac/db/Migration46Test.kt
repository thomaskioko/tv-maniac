package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.readSchema
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration46Test {

    @Test
    fun `should create rewatch session tables given migration to version 46`() {
        openSnapshot(version = 45).use { driver ->
            migrateToVersion(driver, oldVersion = 45, newVersion = 46)

            driver.tableNames() shouldContain "rewatch_session"
            driver.tableNames() shouldContain "rewatch_session_episode"

            val sessionColumns = driver.columnNames("rewatch_session")
            sessionColumns shouldContain "id"
            sessionColumns shouldContain "show_id"
            sessionColumns shouldContain "started_at"
            sessionColumns shouldContain "closed_at"
            sessionColumns shouldContain "provider_session_id"

            val sessionEpisodeColumns = driver.columnNames("rewatch_session_episode")
            sessionEpisodeColumns shouldContain "id"
            sessionEpisodeColumns shouldContain "session_id"
            sessionEpisodeColumns shouldContain "episode_id"
            sessionEpisodeColumns shouldContain "watched_at"
            sessionEpisodeColumns shouldContain "synced_at"
        }
    }

    @Test
    fun `should leave the watched_episodes schema unchanged given migration to version 46`() {
        openSnapshot(version = 45).use { driver ->
            val before = driver.readSchema().first { it.name == "watched_episodes" }

            migrateToVersion(driver, oldVersion = 45, newVersion = 46)

            val after = driver.readSchema().first { it.name == "watched_episodes" }
            after shouldBe before
        }
    }

    @Test
    fun `should keep the watched_episodes unique constraint given migration to version 46`() {
        openSnapshot(version = 45).use { driver ->
            driver.insertShow(tmdbId = 1L)
            driver.insertWatchedEpisode(showId = 1L, seasonNumber = 1L, episodeNumber = 1L)

            migrateToVersion(driver, oldVersion = 45, newVersion = 46)

            shouldThrow<Exception> {
                driver.insertWatchedEpisode(showId = 1L, seasonNumber = 1L, episodeNumber = 1L)
            }
        }
    }

    @Test
    fun `should preserve existing watched_episodes rows given migration to version 46`() {
        openSnapshot(version = 45).use { driver ->
            driver.insertShow(tmdbId = 1L)
            driver.insertWatchedEpisode(showId = 1L, seasonNumber = 1L, episodeNumber = 1L)
            driver.insertWatchedEpisode(showId = 1L, seasonNumber = 1L, episodeNumber = 2L)

            migrateToVersion(driver, oldVersion = 45, newVersion = 46)

            driver.countWatchedEpisodes() shouldBe 2L
        }
    }
}

private fun SqlDriver.insertShow(tmdbId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (tmdb_id, name, overview, ratings, vote_count)
            VALUES ($tmdbId, 'show-$tmdbId', 'overview', 0.0, 0)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.insertWatchedEpisode(showId: Long, seasonNumber: Long, episodeNumber: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO watched_episodes (show_id, season_number, episode_number, watched_at, pending_action)
            VALUES ($showId, $seasonNumber, $episodeNumber, 1700000000000, 'NOTHING')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.countWatchedEpisodes(): Long = executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM watched_episodes",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value
