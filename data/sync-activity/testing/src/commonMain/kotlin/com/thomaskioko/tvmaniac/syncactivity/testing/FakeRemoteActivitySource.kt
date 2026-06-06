package com.thomaskioko.tvmaniac.syncactivity.testing

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.RemoteActivitySource
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

public class FakeRemoteActivitySource : RemoteActivitySource {

    override var provider: ConnectedProvider = ConnectedProvider.TRAKT

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
