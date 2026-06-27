package com.thomaskioko.tvmaniac.presenter.showdetails.trailers

public sealed interface ShowDetailsTrailersAction

public data class ShowDetailsWatchTrailerClicked(val id: Long) : ShowDetailsTrailersAction
