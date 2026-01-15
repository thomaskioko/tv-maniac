package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class SearchShowResult(
    val traktShow: TraktShowResponse,
    val tmdbId: Long,
    val tmdbDetails: TmdbShowDetailsResponse?,
)
