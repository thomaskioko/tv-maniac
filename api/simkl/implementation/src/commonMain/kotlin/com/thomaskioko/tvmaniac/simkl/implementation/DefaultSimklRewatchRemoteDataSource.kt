package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklRewatchRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchCloseRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchItemsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimklRewatchRemoteDataSource(
    @SimklApi private val httpClient: HttpClient,
) : SimklRewatchRemoteDataSource {

    override suspend fun getRewatchSessions(dateFrom: String?): ApiResponse<SimklRewatchItemsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/all-items/shows")
                parameter("allow_rewatch", "yes")
                parameter("extended", "full")
                parameter("episode_watched_at", "yes")
                dateFrom?.let { parameter("date_from", it) }
            }
        }

    override suspend fun addRewatchHistory(request: SimklRewatchHistoryRequest): ApiResponse<SimklRewatchHistoryResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history")
                parameter("allow_rewatch", "yes")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun closeRewatchSession(request: SimklRewatchCloseRequest): ApiResponse<SimklRewatchHistoryResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history")
                parameter("allow_rewatch", "yes")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}
