package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.core.util.network.safeRequest
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class TmdbServiceImpl(
    private val httpClient: HttpClient,
) : TmdbService {

    override suspend fun getTvShowDetails(showId: Int): ApiResponse<ShowDetailResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$showId")
            }
        }

    override suspend fun getEpisodeDetails(
        tmdbShow: Int,
        ssnNumber: Int,
        epNumber: Int
    ): ApiResponse<EpisodesResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$tmdbShow/season/$ssnNumber/episode/$epNumber")
            }
        }


    override suspend fun getTrailers(showId: Int): ApiResponse<TrailersResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$showId/videos")
            }
        }
}
