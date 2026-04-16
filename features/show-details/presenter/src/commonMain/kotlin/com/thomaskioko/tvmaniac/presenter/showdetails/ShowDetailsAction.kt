package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam

public sealed interface ShowDetailsAction

public data object DismissShowsListSheet : ShowDetailsAction

public data object ShowShowsListSheet : ShowDetailsAction

public data object DismissLoginPrompt : ShowDetailsAction

public data object LoginClicked : ShowDetailsAction

public data object ShowCreateListField : ShowDetailsAction

public data object DismissCreateListField : ShowDetailsAction

public data class UpdateCreateListName(val name: String) : ShowDetailsAction

public data object CreateListSubmitted : ShowDetailsAction

public data class ShowDetailsMessageShown(val id: Long) : ShowDetailsAction

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

public data class MarkEpisodeUnwatched(
    val showTraktId: Long,
    val episodeId: Long,
) : ShowDetailsAction

public data class ToggleShowInList(
    val listId: Long,
    val isCurrentlyInList: Boolean,
) : ShowDetailsAction
