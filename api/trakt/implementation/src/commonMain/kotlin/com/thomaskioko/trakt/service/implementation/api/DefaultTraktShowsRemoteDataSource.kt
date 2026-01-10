package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.safeRequest
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktShowsRemoteDataSource(
    private val httpClient: TraktHttpClient,
) : TraktShowsRemoteDataSource {

    override suspend fun getTrendingShows(
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktShowsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/trending")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
        }

    override suspend fun getPopularShows(
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktShowResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/popular")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
        }

    override suspend fun getFavoritedShows(
        page: Int,
        limit: Int,
        period: TimePeriod,
    ): ApiResponse<List<TraktShowsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/favorited/${period.value}")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
        }

    override suspend fun getMostWatchedShows(
        page: Int,
        limit: Int,
        period: TimePeriod,
    ): ApiResponse<List<TraktShowsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/watched/${period.value}")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
        }

    override suspend fun getRelatedShows(
        traktId: Long,
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktShowResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/related")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
        }

    override suspend fun getShowDetails(traktId: Long): ApiResponse<TraktShowResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId")
            }
            parameter("extended", "full")
        }

    override suspend fun getShowSeasons(traktId: Long): ApiResponse<List<TraktSeasonsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons")
            }
            parameter("extended", "full")
        }

    override suspend fun getSeasonEpisodes(
        traktId: Long,
        seasonNumber: Int,
    ): ApiResponse<List<TraktEpisodesResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons/$seasonNumber")
            }
            parameter("extended", "full")
        }

    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("search/tmdb/$tmdbId")
            }
            parameter("type", "show")
            parameter("extended", "full")
        }
}
