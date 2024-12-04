package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.safeRequest
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
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
class DefaultTmdbShowsNetworkDataSource(
  private val httpClient: TmdbHttpClient,
) : TmdbShowsNetworkDataSource {
  override suspend fun getAiringToday(page: Long): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/tv/airing_today")
        parameter("page", "$page")
      }
    }

  override suspend fun getDiscoverShows(page: Long, sortBy: String): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/discover/tv")
        parameter("page", "$page")
        parameter("sort_by", sortBy)
        parameter("include_adult", "false")
        parameter("screened_theatrically", "true")
      }
    }

  override suspend fun getPopularShows(page: Long): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/tv/popular")
        parameter("page", "$page")
      }
    }

  override suspend fun getTopRatedShows(page: Long): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/tv/top_rated")
        parameter("page", "$page")
      }
    }

  override suspend fun getTrendingShows(timeWindow: String): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/trending/tv/$timeWindow")
      }
    }

  override suspend fun getUpComingShows(
    year: Int,
    page: Long,
    sortBy: String,
  ): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/discover/tv")
        parameter("page", "$page")
        parameter("first_air_date_year", year)
        parameter("sort_by", sortBy)
      }
    }

  override suspend fun getUpComingShows(
    page: Long,
    firstAirDate: String,
    lastAirDate: String,
    sortBy: String,
  ): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/discover/tv")
        parameter("page", "$page")
        parameter("first_air_date.gte", firstAirDate)
        parameter("first_air_date.lte", lastAirDate)
        parameter("sort_by", sortBy)
      }
    }

  override suspend fun searchShows(query: String): ApiResponse<TmdbShowResult> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/search/tv")
        parameter("query", query)
      }
    }
}
