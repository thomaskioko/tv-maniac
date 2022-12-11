package com.thomaskioko.tvmaniac.domain.trailers.api

import com.thomaskioko.tvmaniac.domain.trailers.api.model.Trailer

interface TrailersState

object LoadingTrailers : TrailersState

data class TrailersLoaded(
    val selectedVideoKey: String = "",
    val trailersList: List<Trailer> = emptyList(),
) : TrailersState

data class TrailerError(val errorMessage: String) : TrailersState