package com.thomaskioko.tvmaniac.data.rewatch.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public interface RewatchSyncProviderDataSource : SyncProvider {
    public suspend fun supportsRewatch(): Boolean

    public suspend fun readRewatchSessions(providerShowId: Long): ApiResponse<List<RemoteRewatchSession>>

    public suspend fun writeRewatch(write: RemoteRewatchWrite): ApiResponse<RemoteRewatchWriteResult>
}
