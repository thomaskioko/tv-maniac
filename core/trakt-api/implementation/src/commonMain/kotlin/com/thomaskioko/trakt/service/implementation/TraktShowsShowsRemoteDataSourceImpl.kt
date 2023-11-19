package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.trakt.service.implementation.inject.TraktHttpClient
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.safeRequest
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

private const val PAGE_LIMIT_SIZE = 20

@Inject
class TraktShowsShowsRemoteDataSourceImpl(
    private val httpClient: TraktHttpClient,
) : TraktShowsRemoteDataSource {

    override suspend fun getTrendingShows(page: Long): ApiResponse<List<TraktShowsResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/trending")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getRecommendedShows(
        page: Long,
        period: String,
    ): ApiResponse<List<TraktShowsResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/recommended/$period")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getAnticipatedShows(page: Long): ApiResponse<List<TraktShowsResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/anticipated")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getPopularShows(page: Long): ApiResponse<List<TraktShowResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/popular")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getSimilarShows(traktId: Long): ApiResponse<List<TraktShowResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/related")
                parameter("extended", "full")
            }
        }

    override suspend fun getShowSeasons(traktId: Long): ApiResponse<List<TraktSeasonsResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons")
                parameter("extended", "full")
            }
        }

    override suspend fun getSeasonEpisodes(
        traktId: Long,
    ): ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons")
                parameter("extended", "full,episodes")
            }
        }

    override suspend fun getSeasonDetails(traktId: Long): ApiResponse<TraktShowResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId")
                parameter("extended", "full")
            }
        }
}
