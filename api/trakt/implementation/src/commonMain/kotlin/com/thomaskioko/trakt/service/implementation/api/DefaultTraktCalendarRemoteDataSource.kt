package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktCalendarRemoteDataSource(
    private val httpClient: TraktHttpClient,
) : TraktCalendarRemoteDataSource {

    override suspend fun getMyShowsCalendar(
        startDate: String,
        days: Int,
    ): ApiResponse<List<TraktCalendarResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("calendars/my/shows/$startDate/$days")
            }
        }
}
