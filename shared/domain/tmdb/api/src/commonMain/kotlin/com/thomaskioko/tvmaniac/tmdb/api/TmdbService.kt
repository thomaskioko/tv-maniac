package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse

interface TmdbService {

    suspend fun getTvShowDetails(showId: Int): ApiResponse<ShowDetailResponse, ErrorResponse>

    suspend fun getEpisodeDetails(
        tmdbShow: Int,
        ssnNumber: Int,
        epNumber: Int
    ): ApiResponse<EpisodesResponse, ErrorResponse>

    suspend fun getTrailers(showId: Int): ApiResponse<TrailersResponse, ErrorResponse>
}
