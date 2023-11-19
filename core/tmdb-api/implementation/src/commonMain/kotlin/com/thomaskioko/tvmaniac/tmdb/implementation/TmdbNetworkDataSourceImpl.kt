package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.safeRequest
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

@Inject
class TmdbNetworkDataSourceImpl(
    private val httpClient: TmdbHttpClient,
) : TmdbNetworkDataSource {

    override suspend fun getTvShowDetails(showId: Long): ApiResponse<ShowDetailResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$showId")
            }
        }

    override suspend fun getEpisodeDetails(
        tmdbShow: Long,
        ssnNumber: Long,
        epNumber: Long,
    ): ApiResponse<EpisodesResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$tmdbShow/season/$ssnNumber/episode/$epNumber")
            }
        }

    override suspend fun getTrailers(showId: Long): ApiResponse<TrailersResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$showId/videos")
            }
        }
}
