package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val cachedResult = mutableListOf(
    Watchlists(
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
    Watchlists(
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
    Watchlists(
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
    result: List<Watchlists> = updatedData,
) = result
    .map {
        WatchlistItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.first_air_date,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
        )
    }
    .toPersistentList()
