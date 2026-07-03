package com.thomaskioko.tvmaniac.presenter.showdetails.header

public sealed interface ShowDetailsHeaderAction

public data class ShowDetailsFollowClicked(val isInLibrary: Boolean) : ShowDetailsHeaderAction

public data object ShowDetailsOpenShowList : ShowDetailsHeaderAction

public data object ShowRatingClicked : ShowDetailsHeaderAction

public data class RatingSelected(val rating: Int) : ShowDetailsHeaderAction

public data object RatingRemoved : ShowDetailsHeaderAction

public data object RatingSheetDismissed : ShowDetailsHeaderAction
