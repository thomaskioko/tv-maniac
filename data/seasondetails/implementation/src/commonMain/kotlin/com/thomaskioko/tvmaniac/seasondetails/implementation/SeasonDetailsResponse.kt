package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ImagesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodesResponse

internal data class SeasonDetailsResponse(
    val traktEpisodes: List<TraktEpisodesResponse>,
    val tmdbImages: ImagesResponse?,
    val tmdbCredits: CreditsResponse?,
    val tmdbEpisodes: List<EpisodesResponse>,
)
