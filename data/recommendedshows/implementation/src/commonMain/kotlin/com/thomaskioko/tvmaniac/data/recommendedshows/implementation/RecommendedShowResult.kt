package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class RecommendedShowResult(
    val traktShow: TraktShowResponse,
    val tmdbId: Long,
    val tmdbDetails: TmdbShowDetailsResponse?,
)
