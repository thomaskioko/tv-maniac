package com.thomaskioko.tvmaniac.startwatching.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public interface StartWatchingRemoteDataSource : SyncProvider {

    public suspend fun getPlanToWatch(): ApiResponse<List<RemotePlanToWatchShow>>
}
