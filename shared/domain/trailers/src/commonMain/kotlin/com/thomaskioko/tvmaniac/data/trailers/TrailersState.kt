package com.thomaskioko.tvmaniac.data.trailers

import com.thomaskioko.tvmaniac.data.trailers.model.Trailer

interface TrailersState

object LoadingTrailers : TrailersState

data class TrailersLoaded(
    val selectedVideoKey: String = "",
    val trailersList: List<Trailer> = emptyList(),
) : TrailersState

data class TrailerError(val errorMessage: String) : TrailersState