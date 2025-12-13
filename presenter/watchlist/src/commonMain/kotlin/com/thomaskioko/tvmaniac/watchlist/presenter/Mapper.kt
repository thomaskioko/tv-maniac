package com.thomaskioko.tvmaniac.watchlist.presenter

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

fun List<Watchlists>.entityToWatchlistShowList(): PersistentList<WatchlistItem> {
    return this.map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        WatchlistItem(
            tmdbId = it.id.id,
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
}

fun List<SearchWatchlist>.entityToWatchlistShowList(): ImmutableList<WatchlistItem> {
    return this.map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        WatchlistItem(
            tmdbId = it.id.id,
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
}
