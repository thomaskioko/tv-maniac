package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse

public interface TraktCalendarRemoteDataSource {

    public suspend fun getMyShowsCalendar(
        startDate: String,
        days: Int,
    ): ApiResponse<List<TraktCalendarResponse>>
}
