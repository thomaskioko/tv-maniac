package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse

interface TmdbNetworkDataSource {

    suspend fun getTvShowDetails(showId: Long): ApiResponse<ShowDetailResponse>

    suspend fun getEpisodeDetails(
        tmdbShow: Long,
        ssnNumber: Long,
        epNumber: Long,
    ): ApiResponse<EpisodesResponse>

    suspend fun getTrailers(showId: Long): ApiResponse<TrailersResponse>
}
