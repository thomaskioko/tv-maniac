package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.trakt.service.implementation.inject.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.safeRequest
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

@Inject
class TraktUserRemoteDataSourceImpl(
    private val httpClient: TraktHttpClient,
    private val exceptionHandler: NetworkExceptionHandler,
) : TraktUserRemoteDataSource {

    override suspend fun getUser(
        userId: String,
    ): ApiResponse<TraktUserResponse, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("users/$userId")
                parameter("extended", "full")
            }
        }

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> =
        httpClient.get("users/$userId/lists").body()
}
