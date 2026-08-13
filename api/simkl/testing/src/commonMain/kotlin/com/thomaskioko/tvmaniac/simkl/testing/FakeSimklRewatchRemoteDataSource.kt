package com.thomaskioko.tvmaniac.simkl.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklRewatchRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchCloseRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchItemsResponse

public class FakeSimklRewatchRemoteDataSource : SimklRewatchRemoteDataSource {

    private var rewatchSessionsResponse: ApiResponse<SimklRewatchItemsResponse> =
        ApiResponse.Success(SimklRewatchItemsResponse())
    private var addRewatchHistoryResponse: ApiResponse<SimklRewatchHistoryResponse> =
        ApiResponse.Success(SimklRewatchHistoryResponse())

    private var closeRewatchSessionResponse: ApiResponse<SimklRewatchHistoryResponse> =
        ApiResponse.Success(SimklRewatchHistoryResponse())

    public var lastAddRewatchHistoryRequest: SimklRewatchHistoryRequest? = null
        private set

    public var lastCloseRewatchSessionRequest: SimklRewatchCloseRequest? = null
        private set

    public fun setRewatchSessionsResponse(response: ApiResponse<SimklRewatchItemsResponse>) {
        rewatchSessionsResponse = response
    }

    public fun setAddRewatchHistoryResponse(response: ApiResponse<SimklRewatchHistoryResponse>) {
        addRewatchHistoryResponse = response
    }

    public fun setCloseRewatchSessionResponse(response: ApiResponse<SimklRewatchHistoryResponse>) {
        closeRewatchSessionResponse = response
    }

    override suspend fun getRewatchSessions(dateFrom: String?): ApiResponse<SimklRewatchItemsResponse> =
        rewatchSessionsResponse

    override suspend fun addRewatchHistory(request: SimklRewatchHistoryRequest): ApiResponse<SimklRewatchHistoryResponse> {
        lastAddRewatchHistoryRequest = request
        return addRewatchHistoryResponse
    }

    override suspend fun closeRewatchSession(request: SimklRewatchCloseRequest): ApiResponse<SimklRewatchHistoryResponse> {
        lastCloseRewatchSessionRequest = request
        return closeRewatchSessionResponse
    }
}
