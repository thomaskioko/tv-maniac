package com.thomaskioko.tvmaniac.shared.domain.trailers.api.model

data class Trailer(
    val showId : Int,
    val key: String,
    val name: String,
    val youtubeThumbnailUrl: String
)