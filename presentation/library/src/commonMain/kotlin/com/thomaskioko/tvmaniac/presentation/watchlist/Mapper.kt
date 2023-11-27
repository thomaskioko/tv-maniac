package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.core.db.WatchedShow

fun List<WatchedShow>?.entityToWatchlist(): List<LibraryItem> {
    return this?.map {
        LibraryItem(
            traktId = it.show_id.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    } ?: emptyList()
}
