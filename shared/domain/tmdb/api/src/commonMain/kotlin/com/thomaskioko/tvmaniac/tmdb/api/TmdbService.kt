package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse

interface TmdbService {

    suspend fun getTvShowDetails(showId: Int): ShowDetailResponse

    suspend fun getEpisodeDetails(
        tmdbShow: Int,
        ssnNumber: Int,
        epNumber: Int
    ): EpisodesResponse

    suspend fun getTrailers(showId: Int): TrailersResponse
}
