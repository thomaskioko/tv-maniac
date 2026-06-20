package com.thomaskioko.tvmaniac.data.calendar

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderScoped
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public interface CalendarRemoteDataSource : ProviderScoped {

    public suspend fun getCalendarEntries(
        startDate: String,
        days: Int,
    ): ApiResponse<List<RemoteCalendarEntry>>
}
