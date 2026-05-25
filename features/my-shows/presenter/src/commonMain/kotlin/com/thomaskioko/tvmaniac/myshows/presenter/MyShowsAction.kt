package com.thomaskioko.tvmaniac.myshows.presenter

import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption

public sealed interface MyShowsAction

public data class MyShowsShowClicked(val traktId: Long) : MyShowsAction

public data class ChangeMyShowsSortOption(val sortOption: WatchlistSortOption) : MyShowsAction

public data class MyShowsQueryChanged(val query: String) : MyShowsAction

public data object ClearMyShowsQuery : MyShowsAction

public data object ToggleSearchActive : MyShowsAction

public data class ChangeListStyleClicked(val isGridMode: Boolean) : MyShowsAction

public data class MyShowsMessageShown(val id: Long) : MyShowsAction

public data class UpNextEpisodeClicked(val showTraktId: Long, val episodeId: Long) : MyShowsAction

public data class ShowTitleClicked(val showTraktId: Long) : MyShowsAction

public data class MarkUpNextEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : MyShowsAction

public data class UnfollowShowFromUpNext(val showTraktId: Long) : MyShowsAction

public data class OpenSeasonFromUpNext(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : MyShowsAction

public data class RefreshMyShows(val forceRefresh: Boolean = false) : MyShowsAction
