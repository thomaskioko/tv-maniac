package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class ShowDetailsResponse(
    val traktShow: TraktShowResponse,
    val tmdbSeasons: List<SeasonsResponse>,
    val tmdbId: Long,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
)
