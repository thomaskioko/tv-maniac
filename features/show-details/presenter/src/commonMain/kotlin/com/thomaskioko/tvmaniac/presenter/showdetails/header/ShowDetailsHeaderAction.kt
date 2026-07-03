package com.thomaskioko.tvmaniac.presenter.showdetails.header

public sealed interface ShowDetailsHeaderAction

public data class ShowDetailsFollowClicked(val isInLibrary: Boolean) : ShowDetailsHeaderAction

public data object ShowDetailsOpenShowList : ShowDetailsHeaderAction

public data object ShowRatingClicked : ShowDetailsHeaderAction
