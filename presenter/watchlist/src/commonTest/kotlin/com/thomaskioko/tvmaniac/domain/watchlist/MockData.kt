package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val cachedResult = mutableListOf(
    FollowedShows(
        show_trakt_id = Id(84958),
        show_tmdb_id = Id(84958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        status = "Ended",
        year = "2024",
        created_at = 0,
        season_count = null,
        episode_count = null,
        watched_count = 0,
        total_episode_count = 0,
    ),
)

val updatedData = listOf(
    FollowedShows(
        show_trakt_id = Id(84958),
        show_tmdb_id = Id(84958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        status = "Ended",
        year = "2024",
        created_at = 0,
        season_count = null,
        episode_count = null,
        watched_count = 0,
        total_episode_count = 0,
    ),
    FollowedShows(
        show_trakt_id = Id(1232),
        show_tmdb_id = Id(1232),
        name = "The Lazarus Project",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        status = "Ended",
        year = "2024",
        created_at = 0,
        season_count = null,
        episode_count = null,
        watched_count = 0,
        total_episode_count = 0,
    ),
)

internal fun expectedUiResult(
    result: List<FollowedShows> = updatedData,
) = result
    .map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        WatchlistItem(
            traktId = it.show_trakt_id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.year,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
            episodesWatched = watched,
            totalEpisodesTracked = total,
            watchProgress = progress,
        )
    }
    .toPersistentList()
