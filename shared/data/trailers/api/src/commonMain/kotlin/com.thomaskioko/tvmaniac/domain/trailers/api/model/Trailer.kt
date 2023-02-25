package com.thomaskioko.tvmaniac.domain.trailers.api.model

data class Trailer(
    val showId : Long,
    val key: String,
    val name: String,
    val youtubeThumbnailUrl: String
)