package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

public data class TopRatedShowWithImages(
    val traktShow: TraktShowResponse,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
    val pageOrder: Int,
)
