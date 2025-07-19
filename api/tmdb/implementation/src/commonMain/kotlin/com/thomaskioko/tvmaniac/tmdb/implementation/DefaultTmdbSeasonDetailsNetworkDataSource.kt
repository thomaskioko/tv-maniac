package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.safeRequest
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
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
class DefaultTmdbSeasonDetailsNetworkDataSource(
    private val httpClient: TmdbHttpClient,
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
