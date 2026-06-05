package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.insertFollowedShowLegacy
import com.thomaskioko.tvmaniac.db.util.insertTvshow
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.notNullColumns
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration28Test {

    @Test
    fun `should preserve followed_shows rows when migrating past version 27`() {
        openSnapshot(version = 27).use { driver ->
            driver.insertTvshow(traktId = 5001L, tmdbId = 6001L)
            driver.insertTvshow(traktId = 5002L, tmdbId = 6002L)
            driver.insertFollowedShowLegacy(traktId = 5001L, tmdbId = 6001L, followedAt = 1_700_000_000_000L)
            driver.insertFollowedShowLegacy(traktId = 5002L, tmdbId = 6002L, followedAt = 1_700_000_001_000L)

            migrateToCurrent(driver, oldVersion = 27)

            val rows = driver.followedShowsRows()
            rows shouldBe listOf(
                FollowedShowRow(traktId = 5001L, tmdbId = 6001L, followedAt = 1_700_000_000_000L, pendingAction = "NOTHING"),
                FollowedShowRow(traktId = 5002L, tmdbId = 6002L, followedAt = 1_700_000_001_000L, pendingAction = "NOTHING"),
            )
        }
    }

    @Test
    fun `should rebuild user with background_url column`() {
        openSnapshot(version = 27).use { driver ->
            migrateToCurrent(driver, oldVersion = 27)

            val cols = driver.columnNames("user")
            cols shouldContain "slug"
            cols shouldContain "user_name"
            cols shouldContain "full_name"
            cols shouldContain "profile_picture"
            cols shouldContain "background_url"
            cols shouldContain "is_me"
        }
    }

    @Test
    fun `should rebuild trailers with youtube_url column`() {
        openSnapshot(version = 27).use { driver ->
            migrateToCurrent(driver, oldVersion = 27)

            val cols = driver.columnNames("trailers")
            cols shouldContain "youtube_url"
        }
    }

    @Test
    fun `should rebuild show_genres with show_id column`() {
        openSnapshot(version = 27).use { driver ->
            migrateToCurrent(driver, oldVersion = 27)

            val cols = driver.columnNames("show_genres")
            cols shouldContain "show_id"
            cols shouldContain "genre_id"
        }
    }

    @Test
    fun `should make followed_shows tmdb_id nullable`() {
        openSnapshot(version = 27).use { driver ->
            migrateToCurrent(driver, oldVersion = 27)

            val notNullColumns = driver.notNullColumns("followed_shows")
            notNullColumns shouldContain "show_id"
            notNullColumns shouldNotContain "tmdb_id"
        }
    }

    @Test
    fun `should allow null tmdb_id in followed_shows after migration`() {
        openSnapshot(version = 27).use { driver ->
            migrateToCurrent(driver, oldVersion = 27)

            driver.insertTvshow(traktId = 7001L, tmdbId = 8001L)
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO followed_shows (show_id, tmdb_id, followed_at, pending_action)
                    VALUES ((SELECT id FROM tvshow WHERE trakt_id = 7001), NULL, 1700000000000, 'NOTHING')
                """.trimIndent(),
                parameters = 0,
            )

            val rows = driver.followedShowsRows()
            rows shouldBe listOf(
                FollowedShowRow(traktId = 7001L, tmdbId = null, followedAt = 1_700_000_000_000L, pendingAction = "NOTHING"),
            )
        }
    }

    @Test
    fun `should ensure season_videos and episode_image tables exist after migration`() {
        openSnapshot(version = 27).use { driver ->
            migrateToCurrent(driver, oldVersion = 27)

            val tables = driver.tableNames()
            tables shouldContain "season_videos"
            tables shouldContain "episode_image"
        }
    }
}

private data class FollowedShowRow(
    val traktId: Long,
    val tmdbId: Long?,
    val followedAt: Long,
    val pendingAction: String,
)

private fun SqlDriver.followedShowsRows(): List<FollowedShowRow> = executeQuery(
    identifier = null,
    sql = """
        SELECT tvshow.trakt_id, followed_shows.tmdb_id, followed_shows.followed_at, followed_shows.pending_action
        FROM followed_shows
        JOIN tvshow ON tvshow.id = followed_shows.show_id
        ORDER BY tvshow.trakt_id
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<FollowedShowRow>()
        while (cursor.next().value) {
            rows.add(
                FollowedShowRow(
                    traktId = cursor.getLong(0)!!,
                    tmdbId = cursor.getLong(1),
                    followedAt = cursor.getLong(2)!!,
                    pendingAction = cursor.getString(3)!!,
                ),
            )
        }
        QueryResult.Value(rows.toList())
    },
).value
