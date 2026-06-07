package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktShowsRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktShowsRemoteDataSource {

    override suspend fun getTrendingShows(
        page: Int,
        limit: Int,
        genres: String?,
    ): ApiResponse<List<TraktShowsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/trending")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
            if (genres != null) {
                parameter("genres", genres)
            }
        }

    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("genres/shows")
            }
        }

    override suspend fun getPopularShows(
        page: Int,
        limit: Int,
        genres: String?,
    ): ApiResponse<List<TraktShowResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/popular")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
            if (genres != null) {
                parameter("genres", genres)
            }
        }

    override suspend fun getFavoritedShows(
        page: Int,
        limit: Int,
        period: TimePeriod,
        genres: String?,
    ): ApiResponse<List<TraktShowsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/favorited/${period.value}")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
            if (genres != null) {
                parameter("genres", genres)
            }
        }

    override suspend fun getMostWatchedShows(
        page: Int,
        limit: Int,
        period: TimePeriod,
        genres: String?,
    ): ApiResponse<List<TraktShowsResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/watched/${period.value}")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
            if (genres != null) {
                parameter("genres", genres)
            }
        }

    override suspend fun getRelatedShows(
        showId: Long,
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktShowResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$showId/related")
            }
            parameter("page", page)
            parameter("limit", limit)
            parameter("extended", "full")
        }

    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$showId")
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

    override suspend fun searchShows(
        query: String,
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktSearchResult>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("search")
            }
            parameter("type", "show")
            parameter("query", query)
            parameter("extended", "full")
        }

    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$showId/people")
            }
            parameter("extended", "full")
        }

    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$showId/videos")
            }
        }

    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$showId/progress/watched")
            }
            parameter("extended", "full")
        }
}
