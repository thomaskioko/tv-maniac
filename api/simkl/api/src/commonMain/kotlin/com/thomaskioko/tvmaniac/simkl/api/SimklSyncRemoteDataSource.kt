package com.thomaskioko.tvmaniac.simkl.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAllItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSyncHistoryRequest

public interface SimklSyncRemoteDataSource {
    public suspend fun getAllWatchedShows(dateFrom: String? = null): ApiResponse<SimklAllItemsResponse>
    public suspend fun addWatchedHistory(request: SimklSyncHistoryRequest): ApiResponse<SimklAddHistoryResponse>
    public suspend fun removeWatchedHistory(request: SimklSyncHistoryRequest): ApiResponse<SimklRemoveHistoryResponse>
}
