package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse

public interface TraktSyncRemoteDataSource {

    public suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse>
}
