package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktSyncRemoteDataSource(
    private val httpClient: TraktHttpClient,
) : TraktSyncRemoteDataSource {

    override suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/last_activities")
            }
        }
}
