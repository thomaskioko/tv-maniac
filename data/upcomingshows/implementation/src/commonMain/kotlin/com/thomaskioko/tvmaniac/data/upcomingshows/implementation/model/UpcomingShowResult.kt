package com.thomaskioko.tvmaniac.data.upcomingshows.implementation.model

import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

internal data class UpcomingShowResult(
    val tmdbShow: TmdbShowResponse,
    val traktShow: TraktShowResponse?,
)
