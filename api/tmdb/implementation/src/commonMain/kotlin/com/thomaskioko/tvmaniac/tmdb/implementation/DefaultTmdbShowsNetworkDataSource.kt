package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
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
public class DefaultTmdbShowsNetworkDataSource(
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
        voteAverageGte: Double?,
        voteCountGte: Int?,
        firstAirDateGte: String?,
        firstAirDateLte: String?,
    ): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/discover/tv")
                parameter("page", "$page")
                parameter("sort_by", sortBy)
                parameter("include_adult", "false")
                parameter("screened_theatrically", screenedTheatrically)
                parameter("language", "en-US")

                genres?.let { parameter("with_genres", it) }
                watchProviders?.let { parameter("with_watch_providers", it) }
                voteAverageGte?.let { parameter("vote_average.gte", it) }
                voteCountGte?.let { parameter("vote_count.gte", it) }
                firstAirDateGte?.let { parameter("first_air_date.gte", it) }
                firstAirDateLte?.let { parameter("first_air_date.lte", it) }
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

    override suspend fun getShowCredits(tmdbId: Long): ApiResponse<CreditsResponse> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$tmdbId/credits")
            }
        }
    }
}
