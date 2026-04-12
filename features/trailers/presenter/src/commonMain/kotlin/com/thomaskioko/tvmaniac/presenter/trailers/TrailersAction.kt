package com.thomaskioko.tvmaniac.presenter.trailers

public sealed interface TrailersAction

public data object ReloadTrailers : TrailersAction

public data class TrailerSelected(
    val trailerKey: String,
) : TrailersAction

public data class VideoPlayerError(
    val errorMessage: String,
) : TrailersAction
