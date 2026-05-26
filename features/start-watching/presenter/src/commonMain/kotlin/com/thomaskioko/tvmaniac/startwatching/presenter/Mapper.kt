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
                traktId = it.traktId,
                title = it.title,
                posterImageUrl = it.posterPath,
                year = it.year,
                episodeId = it.episodeId,
                episodeTitle = it.episodeTitle,
                episodeNumberFormatted = formatEpisodeNumber(it.seasonNumber, it.episodeNumber),
                seasonNumber = it.seasonNumber,
                episodeNumber = it.episodeNumber,
                runtime = it.runtime?.let { runtime -> "$runtime min" },
                stillImageUrl = it.episodeStillPath,
            )
        }
        .toImmutableList()

private fun formatEpisodeNumber(seasonNumber: Long?, episodeNumber: Long?): String? {
    if (seasonNumber == null || episodeNumber == null) return null
    val season = seasonNumber.toString().padStart(2, '0')
    val episode = episodeNumber.toString().padStart(2, '0')
    return "S$season | E$episode"
}

// Start Watching shows carry no added-date, so ADDED_* keeps the interactor's default order.
private fun sortComparator(sortOption: WatchlistSortOption): Comparator<StartWatchingShow> =
    when (sortOption) {
        WatchlistSortOption.TITLE_ASC -> compareBy { it.title.lowercase() }
        WatchlistSortOption.TITLE_DESC -> compareByDescending { it.title.lowercase() }
        WatchlistSortOption.RELEASED_ASC -> compareBy { it.year ?: "" }
        WatchlistSortOption.RELEASED_DESC -> compareByDescending { it.year ?: "" }
        WatchlistSortOption.ADDED_ASC, WatchlistSortOption.ADDED_DESC -> Comparator { _, _ -> 0 }
    }
