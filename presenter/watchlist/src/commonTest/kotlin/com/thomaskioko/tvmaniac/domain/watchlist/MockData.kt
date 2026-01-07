package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val cachedResult = mutableListOf(
    FollowedShows(
        id = Id(84958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        status = "Ended",
        first_air_date = "2024",
        created_at = 0,
        season_count = null,
        episode_count = null,
        watched_count = 0,
        total_episode_count = 0,
    ),
)

val updatedData = listOf(
    FollowedShows(
        id = Id(84958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        status = "Ended",
        first_air_date = "2024",
        created_at = 0,
        season_count = null,
        episode_count = null,
        watched_count = 0,
        total_episode_count = 0,
    ),
    FollowedShows(
        id = Id(1232),
        name = "The Lazarus Project",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        status = "Ended",
        first_air_date = "2024",
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
            tmdbId = it.show_id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.first_air_date,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
            episodesWatched = watched,
            totalEpisodesTracked = total,
            watchProgress = progress,
        )
    }
    .toPersistentList()
