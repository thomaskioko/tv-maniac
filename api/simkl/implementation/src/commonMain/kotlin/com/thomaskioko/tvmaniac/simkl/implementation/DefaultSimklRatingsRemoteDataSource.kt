package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserRatingsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimklRatingsRemoteDataSource(
    @SimklApi private val httpClient: HttpClient,
) : SimklRatingsRemoteDataSource {

    override suspend fun addRatings(request: SimklRatingsRequest): ApiResponse<SimklAddRatingsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/ratings")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun removeRatings(request: SimklRemoveRatingsRequest): ApiResponse<SimklRemoveRatingsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/ratings/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun getUserShowRatings(): ApiResponse<SimklUserRatingsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/ratings/shows")
            }
        }
}
