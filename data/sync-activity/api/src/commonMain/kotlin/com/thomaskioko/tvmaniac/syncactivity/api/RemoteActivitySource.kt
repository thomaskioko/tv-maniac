package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

/**
 * Provider-neutral access to the remote "last activity" cursor.
 *
 * Reports the most recent server-side timestamp per [ActivityType] for its [provider]. Each backend
 * supplies an adapter contributed into a multibound set; the consumer selects the one whose
 * [provider] is active. [ActivitySyncRepository] consumes these timestamps.
 */
public interface RemoteActivitySource {

    /** The backend this source talks to; used to select the active source from the multibound set. */
    public val provider: ConnectedProvider

    /**
     * Most recent remote timestamp per [ActivityType]. Absent keys carry no server-side activity;
     * [ApiResponse.Unauthenticated] when the active provider has no session.
     */
    public suspend fun getLastActivities(): ApiResponse<Map<ActivityType, Instant>>
}
