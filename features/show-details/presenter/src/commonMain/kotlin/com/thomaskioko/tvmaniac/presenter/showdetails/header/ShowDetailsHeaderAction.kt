package com.thomaskioko.tvmaniac.presenter.showdetails.header

public sealed interface ShowDetailsHeaderAction

public data class ShowDetailsFollowClicked(val isInLibrary: Boolean) : ShowDetailsHeaderAction

public data object ShowDetailsOpenShowList : ShowDetailsHeaderAction

public data object ShowRatingClicked : ShowDetailsHeaderAction

public data object ShowDetailsMoreClicked : ShowDetailsHeaderAction

public data object ShowDetailsMoreDismissed : ShowDetailsHeaderAction

public data object WatchAgainClicked : ShowDetailsHeaderAction

public data object WatchAgainConfirmed : ShowDetailsHeaderAction

public data object WatchAgainDismissed : ShowDetailsHeaderAction

public data object MarkShowWatchedClicked : ShowDetailsHeaderAction

public data object MarkShowWatchedConfirmed : ShowDetailsHeaderAction

public data object MarkShowWatchedDismissed : ShowDetailsHeaderAction
