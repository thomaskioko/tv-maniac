package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
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
public class DefaultTmdbSeasonDetailsNetworkDataSource(
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
