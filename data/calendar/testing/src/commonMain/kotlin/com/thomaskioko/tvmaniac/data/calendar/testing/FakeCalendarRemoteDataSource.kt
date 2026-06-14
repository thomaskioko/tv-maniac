package com.thomaskioko.tvmaniac.data.calendar.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry

public class FakeCalendarRemoteDataSource(
    override val provider: AccountProvider = AccountProvider.TRAKT,
    private val calendarResponse: ApiResponse<List<RemoteCalendarEntry>> = ApiResponse.Success(emptyList()),
) : CalendarRemoteDataSource {

    override suspend fun getCalendarEntries(
        startDate: String,
        days: Int,
    ): ApiResponse<List<RemoteCalendarEntry>> = calendarResponse
}
