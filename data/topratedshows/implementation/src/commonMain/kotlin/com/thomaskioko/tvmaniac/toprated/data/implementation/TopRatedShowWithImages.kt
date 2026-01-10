package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class TopRatedShowWithImages(
    val traktShow: TraktShowResponse,
    val tmdbId: Long,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
    val pageOrder: Int,
)
