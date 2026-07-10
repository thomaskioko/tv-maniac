package com.thomaskioko.tvmaniac.data.calendar

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public interface CalendarRemoteDataSource : SyncProvider {

    public suspend fun getCalendarEntries(
        startDate: String,
        days: Int,
    ): ApiResponse<List<RemoteCalendarEntry>>
}
