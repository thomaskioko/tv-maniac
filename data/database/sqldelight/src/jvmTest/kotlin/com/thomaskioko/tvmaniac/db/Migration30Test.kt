package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import com.thomaskioko.tvmaniac.db.util.WatchProgress
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.queryWatchProgress
import com.thomaskioko.tvmaniac.db.util.seedEpisode
import com.thomaskioko.tvmaniac.db.util.seedSeason
import com.thomaskioko.tvmaniac.db.util.seedTraktContinueWatching
import com.thomaskioko.tvmaniac.db.util.seedTvshow
import com.thomaskioko.tvmaniac.db.util.seedWatchedEpisode
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration30Test {

    @Test
    fun `should drop trakt_watched_shows on migration`() {
        openSnapshot(version = 29).use { driver ->
            migrateToCurrent(driver, oldVersion = 29)

            driver.tableNames().contains("trakt_watched_shows") shouldBe false
        }
    }

    @Test
    fun `should create trakt_continue_watching on migration`() {
        openSnapshot(version = 29).use { driver ->
            migrateToCurrent(driver, oldVersion = 29)

            driver.tableNames().contains("trakt_continue_watching") shouldBe true
        }
    }

    @Test
    fun `should accept upsert into trakt_continue_watching`() {
        openSnapshot(version = 29).use { driver ->
            migrateToCurrent(driver, oldVersion = 29)

            driver.seedTraktContinueWatching(traktId = 1L, tmdbId = 100L)
        }
    }

    @Test
    fun `should delete stale last_requests rows for watched-shows and continue-watching sync types`() {
        openSnapshot(version = 29).use { driver ->
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO last_requests (entity_id, request_type, timestamp)
                    VALUES (33, 'WATCHED_SHOWS_SYNC', 1700000000000)
                """.trimIndent(),
                parameters = 0,
            )
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO last_requests (entity_id, request_type, timestamp)
                    VALUES (33, 'CONTINUE_WATCHING_SYNC', 1700000000000)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToCurrent(driver, oldVersion = 29)

            val remaining = driver.executeQuery(
                identifier = null,
                sql = """
                    SELECT COUNT(*) FROM last_requests
                    WHERE request_type IN ('WATCHED_SHOWS_SYNC', 'CONTINUE_WATCHING_SYNC')
                """.trimIndent(),
                parameters = 0,
                binders = null,
                mapper = { cursor ->
                    cursor.next()
                    QueryResult.Value(cursor.getLong(0) ?: 0L)
                },
            ).value
            remaining shouldBe 0L
        }
    }

    @Test
    fun `should compute show watch progress alongside trakt_continue_watching row`() {
        openSnapshot(version = 29).use { driver ->
            migrateToCurrent(driver, oldVersion = 29)

            driver.seedTvshow(traktId = 1L, tmdbId = 100L)
            driver.seedSeason(id = 11L, showTraktId = 1L, seasonNumber = 1L, episodeCount = 2L)
            driver.seedEpisode(
                id = 111L,
                seasonId = 11L,
                showTraktId = 1L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.seedEpisode(
                id = 112L,
                seasonId = 11L,
                showTraktId = 1L,
                episodeNumber = 2L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.seedTraktContinueWatching(traktId = 1L, tmdbId = 100L)
            driver.seedWatchedEpisode(
                showTraktId = 1L,
                episodeId = 111L,
                seasonNumber = 1L,
                episodeNumber = 1L,
                pendingAction = "NOTHING",
            )

            driver.queryWatchProgress(showTraktId = 1L) shouldBe WatchProgress(watched = 1L, total = 2L)
        }
    }
}

private const val OLD_EPOCH_MS: Long = 1_577_836_800_000L
