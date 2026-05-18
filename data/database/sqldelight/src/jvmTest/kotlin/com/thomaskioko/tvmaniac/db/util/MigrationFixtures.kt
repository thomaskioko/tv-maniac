package com.thomaskioko.tvmaniac.db.util

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver

internal fun SqlDriver.viewNames(): Set<String> = executeQuery(
    identifier = null,
    sql = "SELECT name FROM sqlite_master WHERE type = 'view' ORDER BY name",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val names = mutableSetOf<String>()
        while (cursor.next().value) {
            cursor.getString(0)?.let(names::add)
        }
        QueryResult.Value(names.toSet())
    },
).value

internal fun SqlDriver.seedTvshow(
    traktId: Long,
    tmdbId: Long,
    name: String = "show-$traktId",
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
            VALUES ($traktId, $tmdbId, '$name', 'overview', 0.0, 0)
        """.trimIndent(),
        parameters = 0,
    )
}

internal fun SqlDriver.seedFollowedShow(
    traktId: Long,
    tmdbId: Long,
    followedAt: Long = 1_700_000_000_000L,
    pendingAction: String = "NOTHING",
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO followed_shows (trakt_id, tmdb_id, followed_at, pending_action)
            VALUES ($traktId, $tmdbId, $followedAt, '$pendingAction')
        """.trimIndent(),
        parameters = 0,
    )
}

internal fun SqlDriver.seedTraktWatchedShow(
    traktId: Long,
    tmdbId: Long?,
    plays: Long = 1L,
    lastWatchedAtMs: Long = 1_700_000_000_000L,
    lastUpdatedAtMs: Long = 1_700_000_000_000L,
) {
    val tmdbIdSql = tmdbId?.toString() ?: "NULL"
    execute(
        identifier = null,
        sql = """
            INSERT INTO trakt_watched_shows (trakt_id, tmdb_id, plays, last_watched_at, last_updated_at)
            VALUES ($traktId, $tmdbIdSql, $plays, $lastWatchedAtMs, $lastUpdatedAtMs)
        """.trimIndent(),
        parameters = 0,
    )
}

internal fun SqlDriver.seedSeason(
    id: Long,
    showTraktId: Long,
    seasonNumber: Long,
    title: String = "Season $seasonNumber",
    episodeCount: Long = 1L,
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO season (id, show_trakt_id, season_number, title, episode_count, overview)
            VALUES ($id, $showTraktId, $seasonNumber, '$title', $episodeCount, 'overview')
        """.trimIndent(),
        parameters = 0,
    )
}

internal fun SqlDriver.seedEpisode(
    id: Long,
    seasonId: Long,
    showTraktId: Long,
    episodeNumber: Long,
    firstAired: Long?,
    ratings: Double = 8.0,
    voteCount: Long = 100L,
    title: String = "ep-$id",
) {
    val firstAiredSql = firstAired?.toString() ?: "NULL"
    execute(
        identifier = null,
        sql = """
            INSERT INTO episode (
                id, season_id, show_trakt_id, episode_number,
                title, overview, ratings, vote_count, first_aired
            )
            VALUES (
                $id, $seasonId, $showTraktId, $episodeNumber,
                '$title', 'overview', $ratings, $voteCount, $firstAiredSql
            )
        """.trimIndent(),
        parameters = 0,
    )
}

internal fun SqlDriver.seedWatchedEpisode(
    showTraktId: Long,
    episodeId: Long?,
    seasonNumber: Long,
    episodeNumber: Long,
    pendingAction: String,
    watchedAt: Long = 1_700_000_000_000L,
) {
    val episodeIdSql = episodeId?.toString() ?: "NULL"
    execute(
        identifier = null,
        sql = """
            INSERT INTO watched_episodes (
                show_trakt_id, episode_id, season_number, episode_number,
                watched_at, pending_action
            )
            VALUES (
                $showTraktId, $episodeIdSql, $seasonNumber, $episodeNumber,
                $watchedAt, '$pendingAction'
            )
        """.trimIndent(),
        parameters = 0,
    )
}

internal data class WatchProgress(val watched: Long, val total: Long)

internal fun SqlDriver.queryWatchProgress(showTraktId: Long): WatchProgress = executeQuery(
    identifier = null,
    sql = """
        SELECT watched_count, total_count
        FROM show_watch_progress
        WHERE show_trakt_id = $showTraktId
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        QueryResult.Value(
            if (cursor.next().value) {
                WatchProgress(
                    watched = cursor.getLong(0) ?: 0L,
                    total = cursor.getLong(1) ?: 0L,
                )
            } else {
                WatchProgress(watched = 0L, total = 0L)
            },
        )
    },
).value

internal data class WatchlistRow(
    val showTraktId: Long,
    val showTmdbId: Long?,
    val showName: String?,
    val episodeId: Long?,
    val followedAt: Long?,
)

internal fun SqlDriver.queryNextEpisodesForWatchlist(includeSpecials: Long = 0L): List<WatchlistRow> = executeQuery(
    identifier = null,
    sql = """
        SELECT
            watched_show.trakt_id AS show_trakt_id,
            tvshow.tmdb_id AS show_tmdb_id,
            tvshow.name AS show_name,
            next_episode.episode_id,
            watched_show.last_watched_at AS followed_at
        FROM trakt_watched_shows AS watched_show
        LEFT JOIN tvshow ON watched_show.trakt_id = tvshow.trakt_id
        LEFT JOIN (
            SELECT show_trakt_id, MIN(next_ep_abs_number) AS min_abs_number
            FROM shows_next_to_watch
            WHERE ($includeSpecials = 1 OR (season_number > 0))
            GROUP BY show_trakt_id
        ) AS min_next_episode ON min_next_episode.show_trakt_id = watched_show.trakt_id
        LEFT JOIN shows_next_to_watch AS next_episode
            ON next_episode.show_trakt_id = watched_show.trakt_id
            AND next_episode.next_ep_abs_number = min_next_episode.min_abs_number
        ORDER BY COALESCE(next_episode.last_watched_at, watched_show.last_watched_at) DESC
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<WatchlistRow>()
        while (cursor.next().value) {
            rows.add(
                WatchlistRow(
                    showTraktId = cursor.getLong(0) ?: 0L,
                    showTmdbId = cursor.getLong(1),
                    showName = cursor.getString(2),
                    episodeId = cursor.getLong(3),
                    followedAt = cursor.getLong(4),
                ),
            )
        }
        QueryResult.Value(rows.toList())
    },
).value

internal fun SqlDriver.countNextToWatch(showTraktId: Long): Long = executeQuery(
    identifier = null,
    sql = """
        SELECT COUNT(*)
        FROM shows_next_to_watch
        WHERE show_trakt_id = $showTraktId
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value

internal data class NextToWatchEpisode(
    val episodeId: Long,
    val ratings: Double,
    val voteCount: Long,
)

internal fun SqlDriver.queryFirstNextToWatch(showTraktId: Long): NextToWatchEpisode? = executeQuery(
    identifier = null,
    sql = """
        SELECT episode_id, ratings, vote_count
        FROM shows_next_to_watch
        WHERE show_trakt_id = $showTraktId
        ORDER BY season_number, episode_number
        LIMIT 1
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        QueryResult.Value(
            if (cursor.next().value) {
                NextToWatchEpisode(
                    episodeId = cursor.getLong(0) ?: 0L,
                    ratings = cursor.getDouble(1) ?: 0.0,
                    voteCount = cursor.getLong(2) ?: 0L,
                )
            } else {
                null
            },
        )
    },
).value
