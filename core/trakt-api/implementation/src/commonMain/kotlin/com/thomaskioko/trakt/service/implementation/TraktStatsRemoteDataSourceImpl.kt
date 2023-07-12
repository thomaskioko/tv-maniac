package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.trakt.service.implementation.inject.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.safeRequest
import com.thomaskioko.tvmaniac.trakt.api.TraktStatsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

@Inject
class TraktStatsRemoteDataSourceImpl(
    private val httpClient: TraktHttpClient,
) : TraktStatsRemoteDataSource {

    override suspend fun getStats(
        userId: String,
    ): ApiResponse<TraktUserStatsResponse, ErrorResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userId/stats")
            }
        }
}
