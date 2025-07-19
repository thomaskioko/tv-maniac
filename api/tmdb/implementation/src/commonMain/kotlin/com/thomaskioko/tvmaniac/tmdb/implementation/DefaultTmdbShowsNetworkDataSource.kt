package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.safeRequest
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.implementation.di.TmdbHttpClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTmdbShowsNetworkDataSource(
    private val httpClient: TmdbHttpClient,
) : TmdbShowsNetworkDataSource {
    override suspend fun getAiringToday(page: Long): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/airing_today")
                parameter("page", "$page")
            }
        }
    }

    override suspend fun discoverShows(
        page: Long,
        sortBy: String,
        genres: String?,
        watchProviders: String?,
        screenedTheatrically: Boolean,
    ): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/discover/tv")
                parameter("page", "$page")
                parameter("sort_by", sortBy)
                parameter("include_adult", "false")
                parameter("screened_theatrically", screenedTheatrically)

                genres?.let { parameter("with_genres", it) }
                watchProviders?.let { parameter("with_watch_providers", it) }
            }
        }
    }

    override suspend fun getPopularShows(page: Long): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/popular")
                parameter("page", "$page")
            }
        }
    }

    override suspend fun getTopRatedShows(page: Long): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/top_rated")
                parameter("page", "$page")
            }
        }
    }

    override suspend fun getTrendingShows(timeWindow: String): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/trending/tv/$timeWindow")
            }
        }
    }

    override suspend fun getUpComingShows(
        year: Int,
        page: Long,
        sortBy: String,
    ): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/discover/tv")
                parameter("page", "$page")
                parameter("first_air_date_year", year)
                parameter("sort_by", sortBy)
            }
        }
    }

    override suspend fun getUpComingShows(
        page: Long,
        firstAirDate: String,
        lastAirDate: String,
        sortBy: String,
    ): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/discover/tv")
                parameter("page", "$page")
                parameter("first_air_date.gte", firstAirDate)
                parameter("first_air_date.lte", lastAirDate)
                parameter("sort_by", sortBy)
            }
        }
    }

    override suspend fun searchShows(query: String): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/search/tv")
                parameter("query", query)
            }
        }
    }

    override suspend fun getShowGenres(): ApiResponse<TmdbGenreResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/genre/tv/list")
            }
        }
    }
}
