package com.thomaskioko.tvmaniac.domain.trailers.api

sealed interface TrailersAction

object ReloadTrailers: TrailersAction

data class LoadTrailers(
    val showId: Int,
    val trailerId: String,
) : TrailersAction

data class TrailerSelected(
    val trailerKey: String
) : TrailersAction

data class VideoPlayerError(
    val errorMessage: String
) : TrailersAction