package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration35Test {

    @Test
    fun `should add an internal id and seed a TRAKT external id per show`() {
        openSnapshot(version = 35).use { driver ->
            driver.seedShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.seedShow(traktId = 101, tmdbId = 201, ratings = 9.1, voteCount = 99)

            migrateToCurrent(driver, oldVersion = 35)

            driver.columnNames("tvshow") shouldContain "id"
            val first = driver.showRow(traktId = 100)
            val second = driver.showRow(traktId = 101)
            first.shouldNotBeNull()
            second.shouldNotBeNull()
            (first.id != second.id) shouldBe true
            first.tmdbId shouldBe 200
            driver.externalId(showId = first.id, provider = "TRAKT") shouldBe "100"
            driver.externalId(showId = second.id, provider = "TRAKT") shouldBe "101"
        }
    }

    @Test
    fun `should copy ratings into a TMDB provider_meta row`() {
        openSnapshot(version = 35).use { driver ->
            driver.seedShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)

            migrateToCurrent(driver, oldVersion = 35)

            val show = driver.showRow(traktId = 100)
            show.shouldNotBeNull()
            driver.providerMeta(showId = show.id, provider = "TMDB") shouldBe ProviderMeta(rating = 8.5, voteCount = 1234)
        }
    }

    @Test
    fun `should preserve child rows and pending writes across the tvshow rebuild`() {
        openSnapshot(version = 35).use { driver ->
            driver.seedShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.seedSeasonV35(id = 1, showTraktId = 100, seasonNumber = 1)
            driver.seedEpisodeV35(id = 1, seasonId = 1, showTraktId = 100, episodeNumber = 1, firstAired = 1_700_000_000_000L)
            driver.seedWatchedEpisodeV35(showTraktId = 100, episodeId = 1, seasonNumber = 1, episodeNumber = 1, pendingAction = "UPLOAD")

            migrateToCurrent(driver, oldVersion = 35)

            driver.count("season") shouldBe 1
            driver.count("episode") shouldBe 1
            driver.count("watched_episodes") shouldBe 1
            driver.pendingActionFor(showTraktId = 100) shouldBe "UPLOAD"
        }
    }

    private data class ShowRow(val id: Long, val tmdbId: Long)

    private data class ProviderMeta(val rating: Double, val voteCount: Long)

    private fun SqlDriver.seedShow(traktId: Long, tmdbId: Long, ratings: Double, voteCount: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
                VALUES ($traktId, $tmdbId, 'show-$traktId', 'overview', $ratings, $voteCount)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.seedSeasonV35(id: Long, showTraktId: Long, seasonNumber: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO season (id, show_trakt_id, season_number, title, episode_count, overview)
                VALUES ($id, $showTraktId, $seasonNumber, 'Season $seasonNumber', 1, 'overview')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.seedEpisodeV35(id: Long, seasonId: Long, showTraktId: Long, episodeNumber: Long, firstAired: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO episode (id, season_id, show_trakt_id, episode_number, title, overview, ratings, vote_count, first_aired)
                VALUES ($id, $seasonId, $showTraktId, $episodeNumber, 'ep-$id', 'overview', 8.0, 100, $firstAired)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.seedWatchedEpisodeV35(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        pendingAction: String,
    ) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO watched_episodes (show_trakt_id, episode_id, season_number, episode_number, watched_at, pending_action)
                VALUES ($showTraktId, $episodeId, $seasonNumber, $episodeNumber, 1700000000000, '$pendingAction')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.showRow(traktId: Long): ShowRow? = executeQuery(
        identifier = null,
        sql = "SELECT id, tmdb_id FROM tvshow WHERE trakt_id = $traktId",
        mapper = { cursor ->
            QueryResult.Value(
                if (cursor.next().value) ShowRow(id = cursor.getLong(0)!!, tmdbId = cursor.getLong(1)!!) else null,
            )
        },
        parameters = 0,
    ).value

    private fun SqlDriver.externalId(showId: Long, provider: String): String? = executeQuery(
        identifier = null,
        sql = "SELECT external_id FROM tvshow_external_id WHERE show_id = $showId AND provider = '$provider'",
        mapper = { cursor -> QueryResult.Value(if (cursor.next().value) cursor.getString(0) else null) },
        parameters = 0,
    ).value

    private fun SqlDriver.providerMeta(showId: Long, provider: String): ProviderMeta? = executeQuery(
        identifier = null,
        sql = "SELECT rating, vote_count FROM tvshow_provider_meta WHERE show_id = $showId AND provider = '$provider'",
        mapper = { cursor ->
            QueryResult.Value(
                if (cursor.next().value) {
                    ProviderMeta(rating = cursor.getDouble(0)!!, voteCount = cursor.getLong(1)!!)
                } else {
                    null
                },
            )
        },
        parameters = 0,
    ).value

    private fun SqlDriver.pendingActionFor(showTraktId: Long): String? = executeQuery(
        identifier = null,
        sql = "SELECT pending_action FROM watched_episodes WHERE show_id = (SELECT id FROM tvshow WHERE trakt_id = $showTraktId)",
        mapper = { cursor -> QueryResult.Value(if (cursor.next().value) cursor.getString(0) else null) },
        parameters = 0,
    ).value

    private fun SqlDriver.count(table: String): Long = executeQuery(
        identifier = null,
        sql = "SELECT COUNT(*) FROM $table",
        mapper = { cursor ->
            cursor.next()
            QueryResult.Value(cursor.getLong(0) ?: 0L)
        },
        parameters = 0,
    ).value
}
