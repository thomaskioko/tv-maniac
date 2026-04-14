package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTmdbSeasonDetailsNetworkDataSource(
    @TmdbApi
    private val httpClient: HttpClient,
) : TmdbSeasonDetailsNetworkDataSource {

    override suspend fun getSeasonDetails(
        id: Long,
        seasonNumber: Long,
    ): ApiResponse<TmdbSeasonDetailsResponse> = httpClient.safeRequest {
        url {
            method = HttpMethod.Get
            path("3/tv/$id/season/$seasonNumber")
            parameter("append_to_response", "credits,videos,images")
        }
    }
}
