package com.thomaskioko.tvmaniac.presenter.showdetails.similar

public sealed interface ShowDetailsSimilarAction

public data class ShowDetailsSimilarShowClicked(val showId: Long) : ShowDetailsSimilarAction
