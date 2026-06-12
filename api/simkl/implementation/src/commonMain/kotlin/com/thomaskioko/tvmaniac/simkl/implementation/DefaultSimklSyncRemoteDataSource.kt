package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAllItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSyncHistoryRequest
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
public class DefaultSimklSyncRemoteDataSource(
    @SimklApi private val httpClient: HttpClient,
) : SimklSyncRemoteDataSource {

    override suspend fun getAllWatchedShows(dateFrom: String?): ApiResponse<SimklAllItemsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/all-items/shows")
                parameter("extended", "full")
                parameter("episode_watched_at", "yes")
                dateFrom?.let { parameter("date_from", it) }
            }
        }

    override suspend fun addWatchedHistory(request: SimklSyncHistoryRequest): ApiResponse<SimklAddHistoryResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun removeWatchedHistory(request: SimklSyncHistoryRequest): ApiResponse<SimklRemoveHistoryResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}
