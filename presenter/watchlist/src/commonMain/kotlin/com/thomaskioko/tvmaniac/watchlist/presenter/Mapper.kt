package com.thomaskioko.tvmaniac.watchlist.presenter

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

fun List<Watchlists>.entityToWatchlistShowList(): PersistentList<WatchlistItem> {
    return this.map {
        WatchlistItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            year = it.first_air_date,
            status = it.status,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
        )
    }
        .toPersistentList()
}

fun List<SearchWatchlist>.entityToWatchlistShowList(): ImmutableList<WatchlistItem> {
    return this.map {
        WatchlistItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            year = it.first_air_date,
            status = it.status,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
        )
    }
        .toPersistentList()
}
