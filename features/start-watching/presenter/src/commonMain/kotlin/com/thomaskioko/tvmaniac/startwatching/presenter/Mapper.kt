package com.thomaskioko.tvmaniac.startwatching.presenter

import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun List<StartWatchingShow>.toStartWatchingItems(
    query: String,
    sortOption: WatchlistSortOption,
): ImmutableList<StartWatchingItem> =
    filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }
        .sortedWith(sortComparator(sortOption))
        .map {
            StartWatchingItem(
                showId = it.showId,
                title = it.title,
                posterImageUrl = it.posterPath,
                year = it.year,
            )
        }
        .toImmutableList()

// Start Watching shows carry no added-date, so ADDED_* keeps the interactor's default order.
private fun sortComparator(sortOption: WatchlistSortOption): Comparator<StartWatchingShow> =
    when (sortOption) {
        WatchlistSortOption.TITLE_ASC -> compareBy { it.title.lowercase() }
        WatchlistSortOption.TITLE_DESC -> compareByDescending { it.title.lowercase() }
        WatchlistSortOption.RELEASED_ASC -> compareBy { it.year ?: "" }
        WatchlistSortOption.RELEASED_DESC -> compareByDescending { it.year ?: "" }
        WatchlistSortOption.ADDED_ASC, WatchlistSortOption.ADDED_DESC -> Comparator { _, _ -> 0 }
    }
