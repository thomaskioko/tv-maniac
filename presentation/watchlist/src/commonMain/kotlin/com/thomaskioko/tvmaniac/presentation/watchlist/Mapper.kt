package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.core.db.SelectWatchlist

fun List<SelectWatchlist>?.entityToWatchlist(): List<WatchlistItem> {
    return this?.map {
        WatchlistItem(
            traktId = it.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    } ?: emptyList()
}
