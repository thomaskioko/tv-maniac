package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderScoped
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

public interface RemoteActivitySource : ProviderScoped {

    public suspend fun getLastActivities(): ApiResponse<Map<ActivityType, Instant>>
}
