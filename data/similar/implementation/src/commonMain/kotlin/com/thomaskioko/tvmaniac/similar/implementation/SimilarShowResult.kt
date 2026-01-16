package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class SimilarShowResult(
    val traktShow: TraktShowResponse,
    val tmdbId: Long,
    val tmdbDetails: TmdbShowDetailsResponse?,
)
