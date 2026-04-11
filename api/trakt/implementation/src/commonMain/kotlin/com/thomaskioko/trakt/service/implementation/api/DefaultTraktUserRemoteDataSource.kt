package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktUserRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktUserRemoteDataSource {

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userId")
                parameter("extended", "full")
            }
        }

    override suspend fun getUserStats(userId: String): ApiResponse<TraktUserStatsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userId/stats")
            }
        }

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> =
        httpClient.get("users/$userId/lists").body()
}
