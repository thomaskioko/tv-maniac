package com.thomaskioko.tvmaniac.syncprovider.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncprovider.api.RemoteActivitySource
import kotlin.time.Instant

public class FakeRemoteActivitySource : RemoteActivitySource {

    private var lastActivitiesResponse: ApiResponse<Map<ActivityType, Instant>> =
        ApiResponse.Success(emptyMap())

    public fun setLastActivities(activities: Map<ActivityType, Instant>) {
        lastActivitiesResponse = ApiResponse.Success(activities)
    }

    public fun setLastActivities(response: ApiResponse<Map<ActivityType, Instant>>) {
        lastActivitiesResponse = response
    }

    override suspend fun getLastActivities(): ApiResponse<Map<ActivityType, Instant>> =
        lastActivitiesResponse
}
