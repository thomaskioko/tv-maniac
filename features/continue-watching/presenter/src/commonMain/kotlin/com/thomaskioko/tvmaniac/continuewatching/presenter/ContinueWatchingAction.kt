package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption

public sealed interface ContinueWatchingAction

public data class ContinueWatchingShowClicked(val traktId: Long) : ContinueWatchingAction

public data class ChangeContinueWatchingSortOption(val sortOption: WatchlistSortOption) : ContinueWatchingAction

public data class ContinueWatchingQueryChanged(val query: String) : ContinueWatchingAction

public data object ClearContinueWatchingQuery : ContinueWatchingAction

public data object ToggleContinueWatchingSearch : ContinueWatchingAction

public data class ChangeContinueWatchingListStyle(val isGridMode: Boolean) : ContinueWatchingAction

public data class ContinueWatchingMessageShown(val id: Long) : ContinueWatchingAction

public data class UpNextEpisodeClicked(val showTraktId: Long, val episodeId: Long) : ContinueWatchingAction

public data class ShowTitleClicked(val showTraktId: Long) : ContinueWatchingAction

public data class MarkUpNextEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : ContinueWatchingAction

public data class UnfollowShowFromUpNext(val showTraktId: Long) : ContinueWatchingAction

public data class OpenSeasonFromUpNext(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : ContinueWatchingAction

public data class RefreshContinueWatching(val forceRefresh: Boolean = false) : ContinueWatchingAction
