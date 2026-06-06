package com.thomaskioko.tvmaniac.syncprovider.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

/**
 * Provider-neutral access to the remote "last activity" cursor.
 *
 * Reports the most recent server-side timestamp per [ActivityType] for the active provider, so a
 * checkpoint comparison can decide what to sync. Each backend supplies an adapter; the Trakt adapter
 * is the only one today. [ActivitySyncRepository] consumes these timestamps.
 */
public interface RemoteActivitySource {

    /**
     * Most recent remote timestamp per [ActivityType]. Absent keys carry no server-side activity;
     * [ApiResponse.Unauthenticated] when the active provider has no session.
     */
    public suspend fun getLastActivities(): ApiResponse<Map<ActivityType, Instant>>
}
