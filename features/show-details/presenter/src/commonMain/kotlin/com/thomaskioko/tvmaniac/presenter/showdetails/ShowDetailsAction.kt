package com.thomaskioko.tvmaniac.presenter.showdetails

public sealed interface ShowDetailsAction

public data object ShowDetailsBackClicked : ShowDetailsAction

public data object ShowDetailsReload : ShowDetailsAction

public data class ShowDetailsMessageShown(val id: Long) : ShowDetailsAction
