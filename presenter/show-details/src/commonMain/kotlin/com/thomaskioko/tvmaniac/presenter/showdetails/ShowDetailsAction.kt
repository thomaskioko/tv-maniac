package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam

public sealed interface ShowDetailsAction

public data object DismissShowsListSheet : ShowDetailsAction

public data object ShowShowsListSheet : ShowDetailsAction

public data object CreateCustomList : ShowDetailsAction

public data object DismissErrorSnackbar : ShowDetailsAction

public data object DetailBackClicked : ShowDetailsAction

public data object ReloadShowDetails : ShowDetailsAction

public data class SeasonClicked(val params: ShowSeasonDetailsParam) : ShowDetailsAction

public data class DetailShowClicked(val id: Long) : ShowDetailsAction

public data class WatchTrailerClicked(val id: Long) : ShowDetailsAction

public data class FollowShowClicked(val isInLibrary: Boolean) : ShowDetailsAction

public data class MarkEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : ShowDetailsAction
