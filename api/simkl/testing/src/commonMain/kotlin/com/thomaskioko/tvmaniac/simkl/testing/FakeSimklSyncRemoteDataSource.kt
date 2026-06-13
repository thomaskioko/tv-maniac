package com.thomaskioko.tvmaniac.simkl.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAllItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklLastActivitiesResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSyncHistoryRequest

public class FakeSimklSyncRemoteDataSource(
    private var lastActivitiesResponse: ApiResponse<SimklLastActivitiesResponse> = ApiResponse.Unauthenticated,
    private var allWatchedShowsResponse: ApiResponse<SimklAllItemsResponse> = ApiResponse.Unauthenticated,
    private var addHistoryResponse: ApiResponse<SimklAddHistoryResponse> = ApiResponse.Unauthenticated,
    private var removeHistoryResponse: ApiResponse<SimklRemoveHistoryResponse> = ApiResponse.Unauthenticated,
) : SimklSyncRemoteDataSource {

    public fun setLastActivities(response: ApiResponse<SimklLastActivitiesResponse>) {
        lastActivitiesResponse = response
    }

    public fun setAllWatchedShows(response: ApiResponse<SimklAllItemsResponse>) {
        allWatchedShowsResponse = response
    }

    public fun setAddHistoryResponse(response: ApiResponse<SimklAddHistoryResponse>) {
        addHistoryResponse = response
    }

    public fun setRemoveHistoryResponse(response: ApiResponse<SimklRemoveHistoryResponse>) {
        removeHistoryResponse = response
    }

    override suspend fun getLastActivities(): ApiResponse<SimklLastActivitiesResponse> =
        lastActivitiesResponse

    override suspend fun getAllWatchedShows(dateFrom: String?): ApiResponse<SimklAllItemsResponse> =
        allWatchedShowsResponse

    override suspend fun addWatchedHistory(request: SimklSyncHistoryRequest): ApiResponse<SimklAddHistoryResponse> =
        addHistoryResponse

    override suspend fun removeWatchedHistory(request: SimklSyncHistoryRequest): ApiResponse<SimklRemoveHistoryResponse> =
        removeHistoryResponse
}
