package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktSyncRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktSyncRemoteDataSource {

    override suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/last_activities")
            }
        }

    override suspend fun getWatchedShows(limit: String): ApiResponse<List<TraktWatchedShowResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/watched/shows")
                parameters.append("extended", "progress")
                parameters.append("limit", limit)
            }
        }
}
