package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse

public interface TraktSyncRemoteDataSource {

    public suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse>

    public suspend fun getWatchedShows(): ApiResponse<List<TraktWatchedShowResponse>>
}
