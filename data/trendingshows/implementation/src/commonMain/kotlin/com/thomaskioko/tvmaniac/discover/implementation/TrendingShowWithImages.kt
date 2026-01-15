package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse

internal data class TrendingShowWithImages(
    val traktShow: TraktShowsResponse,
    val tmdbId: Long,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
    val pageOrder: Int,
)
