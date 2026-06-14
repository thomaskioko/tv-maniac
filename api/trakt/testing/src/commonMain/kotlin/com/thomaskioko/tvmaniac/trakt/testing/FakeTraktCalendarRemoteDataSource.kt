package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse

public class FakeTraktCalendarRemoteDataSource(
    private var calendarResponse: ApiResponse<List<TraktCalendarResponse>> = ApiResponse.Success(emptyList()),
) : TraktCalendarRemoteDataSource {

    public fun setCalendarEntries(response: ApiResponse<List<TraktCalendarResponse>>) {
        calendarResponse = response
    }

    override suspend fun getMyShowsCalendar(
        startDate: String,
        days: Int,
    ): ApiResponse<List<TraktCalendarResponse>> = calendarResponse
}
