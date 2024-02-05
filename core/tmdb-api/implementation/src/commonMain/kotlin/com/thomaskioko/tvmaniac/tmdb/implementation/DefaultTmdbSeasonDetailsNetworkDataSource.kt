package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.safeRequest
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultTmdbSeasonDetailsNetworkDataSource(
  private val httpClient: TmdbHttpClient,
) : TmdbSeasonDetailsNetworkDataSource {

  override suspend fun getSeasonDetails(
    id: Long,
    seasonNumber: Long,
  ): ApiResponse<TmdbSeasonDetailsResponse> =
    httpClient.safeRequest {
      url {
        method = HttpMethod.Get
        path("3/tv/$id/season/$seasonNumber")
        parameter("append_to_response", "credits,videos,images")
      }
    }
}
