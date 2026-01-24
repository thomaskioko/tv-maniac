package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHistoryEntry
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncResponse
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktEpisodeRemoteDataSource(
    private val httpClient: TraktHttpClient,
) : TraktEpisodeHistoryRemoteDataSource {

    override suspend fun getShowEpisodeWatches(showTraktId: Long): ApiResponse<List<TraktHistoryEntry>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("users/me/history/shows/$showTraktId")
                parameters.append("extended", "noseasons")
                parameters.append("limit", "10000")
            }
        }

    override suspend fun addEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history")
            }
            contentType(ContentType.Application.Json)
            setBody(items)
        }

    override suspend fun removeEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Post
                path("sync/history/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(items)
        }
}
