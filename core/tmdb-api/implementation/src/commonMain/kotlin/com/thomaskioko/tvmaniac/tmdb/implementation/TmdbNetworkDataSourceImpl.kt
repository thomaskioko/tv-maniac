package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.safeRequest
import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

@Inject
class TmdbNetworkDataSourceImpl(
    private val httpClient: TmdbHttpClient,
    private val exceptionHandler: NetworkExceptionHandler,
) : TmdbNetworkDataSource {

    override suspend fun getTvShowDetails(showId: Long): ApiResponse<ShowDetailResponse, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
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
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("3/tv/$tmdbShow/season/$ssnNumber/episode/$epNumber")
            }
        }

    override suspend fun getTrailers(showId: Long): ApiResponse<TrailersResponse, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("3/tv/$showId/videos")
            }
        }
}
