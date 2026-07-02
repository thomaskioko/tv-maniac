package com.thomaskioko.tvmaniac.simkl.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsCountBucket
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowSummaryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserRatingsResponse

public class FakeSimklRatingsRemoteDataSource : SimklRatingsRemoteDataSource {

    private var addRatingsResponse: ApiResponse<SimklAddRatingsResponse> =
        ApiResponse.Success(SimklAddRatingsResponse(added = SimklRatingsCountBucket(shows = 1)))
    private var removeRatingsResponse: ApiResponse<SimklRemoveRatingsResponse> =
        ApiResponse.Success(SimklRemoveRatingsResponse(deleted = SimklRatingsCountBucket(shows = 1)))
    private var userShowRatingsResponse: ApiResponse<SimklUserRatingsResponse> =
        ApiResponse.Success(SimklUserRatingsResponse())
    private var showSummaryResponse: ApiResponse<SimklShowSummaryResponse> =
        ApiResponse.Success(SimklShowSummaryResponse())

    public fun setAddRatingsResponse(response: ApiResponse<SimklAddRatingsResponse>) {
        addRatingsResponse = response
    }

    public fun setRemoveRatingsResponse(response: ApiResponse<SimklRemoveRatingsResponse>) {
        removeRatingsResponse = response
    }

    public fun setUserShowRatingsResponse(response: ApiResponse<SimklUserRatingsResponse>) {
        userShowRatingsResponse = response
    }

    public fun setShowSummaryResponse(response: ApiResponse<SimklShowSummaryResponse>) {
        showSummaryResponse = response
    }

    override suspend fun addRatings(request: SimklRatingsRequest): ApiResponse<SimklAddRatingsResponse> = addRatingsResponse

    override suspend fun removeRatings(request: SimklRemoveRatingsRequest): ApiResponse<SimklRemoveRatingsResponse> = removeRatingsResponse

    override suspend fun getUserShowRatings(): ApiResponse<SimklUserRatingsResponse> = userShowRatingsResponse

    override suspend fun getShowSummary(simklId: Long): ApiResponse<SimklShowSummaryResponse> = showSummaryResponse
}
