package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.enableForeignKeys
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.seedTvshow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration25Test {

    @Test
    fun `should drop cached count columns from show_metadata when migrating past version 24`() {
        openSnapshot(version = 24).use { driver ->
            driver.columnNames("show_metadata") shouldContain "cached_watched_count"
            driver.columnNames("show_metadata") shouldContain "cached_total_count"

            migrateToCurrent(driver, oldVersion = 24)

            driver.columnNames("show_metadata") shouldNotContain "cached_watched_count"
            driver.columnNames("show_metadata") shouldNotContain "cached_total_count"
        }
    }

    @Test
    fun `should preserve unrelated show_metadata columns`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            val cols = driver.columnNames("show_metadata")
            cols shouldContain "show_trakt_id"
            cols shouldContain "season_count"
            cols shouldContain "episode_count"
            cols shouldContain "status"
            cols shouldContain "last_watched_episode_id"
            cols shouldContain "last_watched_season_number"
            cols shouldContain "last_watched_episode_number"
            cols shouldContain "last_watched_at"
        }
    }

    @Test
    fun `should preserve row data across the column drop`() {
        openSnapshot(version = 24).use { driver ->
            driver.seedTvshow(traktId = 42L, tmdbId = 4242L)
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO show_metadata (
                        show_trakt_id, season_count, episode_count, status,
                        cached_watched_count, cached_total_count,
                        last_watched_episode_id, last_watched_season_number,
                        last_watched_episode_number, last_watched_at
                    ) VALUES (42, 5, 50, 'Returning Series', 17, 50, 1234, 3, 4, 1700000000000)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToCurrent(driver, oldVersion = 24)

            val row = driver.executeQuery(
                identifier = null,
                sql = """
                    SELECT show_trakt_id, season_count, episode_count, status,
                           last_watched_episode_id, last_watched_season_number,
                           last_watched_episode_number, last_watched_at
                    FROM show_metadata WHERE show_trakt_id = 42
                """.trimIndent(),
                parameters = 0,
                binders = null,
                mapper = { cursor ->
                    QueryResult.Value(
                        if (cursor.next().value) {
                            ShowMetadataRow(
                                showTraktId = cursor.getLong(0)!!,
                                seasonCount = cursor.getLong(1)!!,
                                episodeCount = cursor.getLong(2)!!,
                                status = cursor.getString(3),
                                lastWatchedEpisodeId = cursor.getLong(4),
                                lastWatchedSeasonNumber = cursor.getLong(5),
                                lastWatchedEpisodeNumber = cursor.getLong(6),
                                lastWatchedAt = cursor.getLong(7),
                            )
                        } else {
                            null
                        },
                    )
                },
            ).value

            row shouldBe ShowMetadataRow(
                showTraktId = 42L,
                seasonCount = 5L,
                episodeCount = 50L,
                status = "Returning Series",
                lastWatchedEpisodeId = 1234L,
                lastWatchedSeasonNumber = 3L,
                lastWatchedEpisodeNumber = 4L,
                lastWatchedAt = 1700000000000L,
            )
        }
    }

    @Test
    fun `should retain show_metadata foreign key on tvshow`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.enableForeignKeys()
            val error = runCatching {
                driver.execute(
                    identifier = null,
                    sql = "INSERT INTO show_metadata (show_trakt_id) VALUES (999999999)",
                    parameters = 0,
                )
            }.exceptionOrNull()
            val message = generateSequence(error) { it.cause }.joinToString { it.message.orEmpty() }
            message.contains("FOREIGN KEY", ignoreCase = true) shouldBe true
        }
    }
}

private data class ShowMetadataRow(
    val showTraktId: Long,
    val seasonCount: Long,
    val episodeCount: Long,
    val status: String?,
    val lastWatchedEpisodeId: Long?,
    val lastWatchedSeasonNumber: Long?,
    val lastWatchedEpisodeNumber: Long?,
    val lastWatchedAt: Long?,
)
