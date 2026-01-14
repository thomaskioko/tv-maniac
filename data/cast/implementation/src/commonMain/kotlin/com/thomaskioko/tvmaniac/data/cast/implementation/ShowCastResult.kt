package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse

/**
 * Intermediate result from fetching show cast data.
 *
 * @property showTraktId The Trakt ID of the show
 * @property traktPeople The people response from Trakt API
 * @property tmdbCredits The credits response from TMDB API (for profile images)
 */
internal data class ShowCastResult(
    val showTraktId: Long,
    val traktPeople: TraktShowPeopleResponse,
    val tmdbCredits: CreditsResponse?,
)
