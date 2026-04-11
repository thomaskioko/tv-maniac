package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHistoryEntry
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktEpisodeRemoteDataSource(
    private val httpClient: TraktHttpClient,
) : TraktEpisodeHistoryRemoteDataSource {

    override suspend fun getShowEpisodeWatches(showTraktId: Long): ApiResponse<List<TraktHistoryEntry>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/me/history/shows/$showTraktId")
                parameters.append("extended", "noseasons")
                parameters.append("limit", "10000")
            }
        }

    override suspend fun addEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history")
            }
            contentType(ContentType.Application.Json)
            setBody(items)
        }

    override suspend fun removeEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(items)
        }
}
