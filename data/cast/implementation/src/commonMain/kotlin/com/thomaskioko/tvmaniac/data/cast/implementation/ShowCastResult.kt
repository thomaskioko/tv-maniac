package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse

internal data class ShowCastResult(
    val showId: Long,
    val traktPeople: TraktShowPeopleResponse?,
    val tmdbCredits: CreditsResponse?,
)
