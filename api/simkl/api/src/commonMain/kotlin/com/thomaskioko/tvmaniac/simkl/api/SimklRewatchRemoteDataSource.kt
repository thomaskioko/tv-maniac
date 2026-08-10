package com.thomaskioko.tvmaniac.simkl.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchCloseRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchItemsResponse

public interface SimklRewatchRemoteDataSource {
    public suspend fun getRewatchSessions(dateFrom: String? = null): ApiResponse<SimklRewatchItemsResponse>

    public suspend fun addRewatchHistory(request: SimklRewatchHistoryRequest): ApiResponse<SimklRewatchHistoryResponse>

    public suspend fun closeRewatchSession(request: SimklRewatchCloseRequest): ApiResponse<SimklRewatchHistoryResponse>
}
