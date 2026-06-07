package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration35Test {

    @Test
    fun `should add an internal id and seed a TRAKT external id per show`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertShow(traktId = 101, tmdbId = 201, ratings = 9.1, voteCount = 99)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

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
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val show = driver.showRow(traktId = 100)
            show.shouldNotBeNull()
            driver.providerMeta(showId = show.id, provider = "TMDB") shouldBe ProviderMeta(rating = 8.5, voteCount = 1234)
        }
    }

    @Test
    fun `should preserve child rows and pending writes across the tvshow rebuild`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertSeasonV35(id = 1, showTraktId = 100, seasonNumber = 1)
            driver.insertEpisodeV35(id = 1, seasonId = 1, showTraktId = 100, episodeNumber = 1, firstAired = 1_700_000_000_000L)
            driver.insertWatchedEpisodeV35(showTraktId = 100, episodeId = 1, seasonNumber = 1, episodeNumber = 1, pendingAction = "UPLOAD")

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            driver.count("season") shouldBe 1
            driver.count("episode") shouldBe 1
            driver.count("watched_episodes") shouldBe 1
            driver.pendingActionFor(showTraktId = 100) shouldBe "UPLOAD"
        }
    }

    private data class ShowRow(val id: Long, val tmdbId: Long)

    private data class ProviderMeta(val rating: Double, val voteCount: Long)

    private fun SqlDriver.insertShow(traktId: Long, tmdbId: Long, ratings: Double, voteCount: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
                VALUES ($traktId, $tmdbId, 'show-$traktId', 'overview', $ratings, $voteCount)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertSeasonV35(id: Long, showTraktId: Long, seasonNumber: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO season (id, show_trakt_id, season_number, title, episode_count, overview)
                VALUES ($id, $showTraktId, $seasonNumber, 'Season $seasonNumber', 1, 'overview')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertEpisodeV35(id: Long, seasonId: Long, showTraktId: Long, episodeNumber: Long, firstAired: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO episode (id, season_id, show_trakt_id, episode_number, title, overview, ratings, vote_count, first_aired)
                VALUES ($id, $seasonId, $showTraktId, $episodeNumber, 'ep-$id', 'overview', 8.0, 100, $firstAired)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertWatchedEpisodeV35(
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
        sql = """
            SELECT tvshow.id, tvshow.tmdb_id FROM tvshow
            JOIN show_trakt ON show_trakt.show_id = tvshow.id
            WHERE show_trakt.trakt_id = $traktId
        """.trimIndent(),
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
        sql = "SELECT pending_action FROM watched_episodes WHERE show_id = (SELECT show_id FROM show_trakt WHERE trakt_id = $showTraktId)",
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

    @Test
    fun `should repoint discovery lists onto the internal show id`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertUpcomingShowV35(traktId = 100, tmdbId = 200)
            driver.insertTrendingShowV35(traktId = 100, tmdbId = 200)
            driver.insertGenreShowV35(genreSlug = "action", traktId = 100)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val internalId = driver.showRow(traktId = 100)!!.id

            val upcomingShowId = driver.singleLong("SELECT show_id FROM upcoming_shows LIMIT 1")
            upcomingShowId shouldBe internalId
            driver.count("upcoming_shows") shouldBe 1
            driver.columnNames("upcoming_shows") shouldContain "show_id"
            driver.columnNames("upcoming_shows") shouldNotContain "trakt_id"

            val trendingShowId = driver.singleLong("SELECT show_id FROM trending_shows LIMIT 1")
            trendingShowId shouldBe internalId
            driver.count("trending_shows") shouldBe 1
            driver.columnNames("trending_shows") shouldContain "show_id"
            driver.columnNames("trending_shows") shouldNotContain "trakt_id"

            val genreShowId = driver.singleLong("SELECT show_id FROM genre_shows LIMIT 1")
            genreShowId shouldBe internalId
            driver.count("genre_shows") shouldBe 1
            driver.columnNames("genre_shows") shouldContain "show_id"
            driver.columnNames("genre_shows") shouldNotContain "trakt_id"
        }
    }

    @Test
    fun `should repoint the library and rename continue watching onto the internal show id`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertFollowedShowV35(traktId = 100, tmdbId = 200, pendingAction = "UPLOAD")
            driver.insertTraktContinueWatchingV35(traktId = 100, tmdbId = 200)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val internalId = driver.showRow(traktId = 100)!!.id

            val followedShowId = driver.singleLong("SELECT show_id FROM followed_shows LIMIT 1")
            followedShowId shouldBe internalId
            val pendingAction = driver.singleString("SELECT pending_action FROM followed_shows WHERE show_id = $internalId")
            pendingAction shouldBe "UPLOAD"

            driver.tableNames() shouldNotContain "trakt_continue_watching"
            driver.tableNames() shouldContain "continue_watching"

            val cwShowId = driver.singleLong("SELECT show_id FROM continue_watching LIMIT 1")
            cwShowId shouldBe internalId
        }
    }

    @Test
    fun `should repoint favorites onto the internal show id`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertFavoriteShowV35(showTraktId = 100, rank = 0, listedAt = "2026-01-01")

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val internalId = driver.showRow(traktId = 100)!!.id

            driver.count("favorite_shows") shouldBe 1
            val favoriteShowId = driver.singleLong("SELECT show_id FROM favorite_shows LIMIT 1")
            favoriteShowId shouldBe internalId
        }
    }

    @Test
    fun `should repoint tmdb foreign key tables onto the internal show id while keeping tmdb id`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertGenreV35(id = 1, name = "Drama")
            driver.insertShowGenreV35(showTmdbId = 200, genreId = 1)
            driver.insertTrailerV35(id = "t1", showTmdbId = 200)
            driver.insertWatchProviderV35(id = 1, tmdbId = 200, traktId = 100)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val internalId = driver.showRow(traktId = 100)!!.id

            val showGenreShowId = driver.singleLong("SELECT show_id FROM show_genres LIMIT 1")
            showGenreShowId shouldBe internalId

            val trailerShowId = driver.singleLong("SELECT show_id FROM trailers WHERE id = 't1'")
            trailerShowId shouldBe internalId

            val wpShowId = driver.singleLong("SELECT show_id FROM watch_providers WHERE id = 1")
            wpShowId shouldBe internalId

            val wpTmdbId = driver.singleLong("SELECT tmdb_id FROM watch_providers WHERE id = 1")
            wpTmdbId shouldBe 200L

            driver.columnNames("watch_providers") shouldNotContain "trakt_id"
        }
    }

    @Test
    fun `should drop the trakt id column from tvshow`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val cols = driver.columnNames("tvshow")
            cols shouldContain "id"
            cols shouldContain "tmdb_id"
            cols shouldNotContain "trakt_id"
        }
    }

    @Test
    fun `should drop unresolvable library and continue watching rows`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertFollowedShowV35(traktId = 100, tmdbId = 200, pendingAction = "NOTHING")
            driver.insertFollowedShowV35(traktId = 999, tmdbId = 999, pendingAction = "UPLOAD")
            driver.insertTraktContinueWatchingV35(traktId = 100, tmdbId = 200)
            driver.insertTraktContinueWatchingV35(traktId = 999, tmdbId = 999)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            driver.count("followed_shows") shouldBe 1
            driver.count("continue_watching") shouldBe 1
        }
    }

    @Test
    fun `should keep the trakt id boundary resolvable through the show_trakt view after migration`() {
        openSnapshot(version = 35).use { driver ->
            driver.insertShow(traktId = 100, tmdbId = 200, ratings = 8.5, voteCount = 1234)
            driver.insertFollowedShowV35(traktId = 100, tmdbId = 200, pendingAction = "NOTHING")
            driver.insertUpcomingShowV35(traktId = 100, tmdbId = 200)

            migrateToVersion(driver, oldVersion = 35, newVersion = 36)

            val followedCount = driver.singleLong(
                "SELECT COUNT(*) FROM followed_shows JOIN show_trakt ON show_trakt.show_id = followed_shows.show_id WHERE show_trakt.trakt_id = 100",
            )
            followedCount shouldBe 1

            val upcomingCount = driver.singleLong(
                "SELECT COUNT(*) FROM upcoming_shows JOIN show_trakt ON show_trakt.show_id = upcoming_shows.show_id WHERE show_trakt.trakt_id = 100",
            )
            upcomingCount shouldBe 1
        }
    }

    private fun SqlDriver.insertUpcomingShowV35(traktId: Long, tmdbId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO upcoming_shows (trakt_id, tmdb_id, page, name, poster_path, overview, page_order)
                VALUES ($traktId, $tmdbId, 1, 'show-$traktId', NULL, 'overview', 0)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertTrendingShowV35(traktId: Long, tmdbId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO trending_shows (trakt_id, tmdb_id, page, position, name, poster_path, overview)
                VALUES ($traktId, $tmdbId, 1, 1, 'show-$traktId', NULL, 'overview')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertGenreShowV35(genreSlug: String, traktId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO genre_shows (genre_slug, trakt_id, page_order, category)
                VALUES ('$genreSlug', $traktId, 0, 'POPULAR')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertFollowedShowV35(traktId: Long, tmdbId: Long, pendingAction: String) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO followed_shows (trakt_id, tmdb_id, followed_at, pending_action)
                VALUES ($traktId, $tmdbId, 1700000000000, '$pendingAction')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertTraktContinueWatchingV35(traktId: Long, tmdbId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO trakt_continue_watching (trakt_id, tmdb_id, aired_episodes, completed_count, last_watched_at, last_updated_at)
                VALUES ($traktId, $tmdbId, 10, 1, 1700000000000, 1700000000000)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertFavoriteShowV35(showTraktId: Long, rank: Long, listedAt: String) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO favorite_shows (show_trakt_id, rank, listed_at)
                VALUES ($showTraktId, $rank, '$listedAt')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertGenreV35(id: Long, name: String) {
        execute(
            identifier = null,
            sql = "INSERT INTO genres (id, name) VALUES ($id, '$name')",
            parameters = 0,
        )
    }

    private fun SqlDriver.insertShowGenreV35(showTmdbId: Long, genreId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO show_genres (show_tmdb_id, genre_id)
                VALUES ($showTmdbId, $genreId)
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertTrailerV35(id: String, showTmdbId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO trailers (id, show_tmdb_id, youtube_url, name, site, size, type)
                VALUES ('$id', $showTmdbId, 'https://youtube.com/watch?v=$id', 'Trailer', 'YouTube', 1080, 'Trailer')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.insertWatchProviderV35(id: Long, tmdbId: Long, traktId: Long) {
        execute(
            identifier = null,
            sql = """
                INSERT INTO watch_providers (id, tmdb_id, trakt_id, logo_path, name)
                VALUES ($id, $tmdbId, $traktId, NULL, 'provider-$id')
            """.trimIndent(),
            parameters = 0,
        )
    }

    private fun SqlDriver.singleLong(sql: String): Long = executeQuery(
        identifier = null,
        sql = sql,
        mapper = { cursor ->
            cursor.next()
            QueryResult.Value(cursor.getLong(0) ?: 0L)
        },
        parameters = 0,
    ).value

    private fun SqlDriver.singleString(sql: String): String? = executeQuery(
        identifier = null,
        sql = sql,
        mapper = { cursor -> QueryResult.Value(if (cursor.next().value) cursor.getString(0) else null) },
        parameters = 0,
    ).value
}
