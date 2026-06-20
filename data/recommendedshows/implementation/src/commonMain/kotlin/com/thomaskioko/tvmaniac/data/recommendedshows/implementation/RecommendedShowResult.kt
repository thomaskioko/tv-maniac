package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class RecommendedShowResult(
    val tmdbId: Long,
    val traktShow: TraktShowResponse? = null,
    val tmdbShow: TmdbShowResponse? = null,
    val tmdbDetails: TmdbShowDetailsResponse? = null,
)
