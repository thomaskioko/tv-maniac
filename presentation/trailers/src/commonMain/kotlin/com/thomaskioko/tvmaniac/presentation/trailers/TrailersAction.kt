package com.thomaskioko.tvmaniac.presentation.trailers

sealed interface TrailersAction

object ReloadTrailers : TrailersAction

data class LoadTrailers(
    val showId: Long,
    val trailerId: String,
) : TrailersAction

data class TrailerSelected(
    val trailerKey: String,
) : TrailersAction

data class VideoPlayerError(
    val errorMessage: String,
) : TrailersAction
