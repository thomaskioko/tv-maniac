package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.di.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktCalendarRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktCalendarRemoteDataSource {

    override suspend fun getMyShowsCalendar(
        startDate: String,
        days: Int,
    ): ApiResponse<List<TraktCalendarResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("calendars/my/shows/$startDate/$days")
            }
            parameter("extended", "full")
        }
}
