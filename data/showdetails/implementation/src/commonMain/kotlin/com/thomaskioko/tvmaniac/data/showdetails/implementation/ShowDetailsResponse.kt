package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class ShowDetailsResponse(
    val traktShow: TraktShowResponse,
    val traktSeasons: List<TraktSeasonsResponse>,
    val tmdbId: Long,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
)
