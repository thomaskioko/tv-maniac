package com.thomaskioko.tvmaniac.simkl.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowSummaryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserRatingsResponse

public interface SimklRatingsRemoteDataSource {
    public suspend fun addRatings(request: SimklRatingsRequest): ApiResponse<SimklAddRatingsResponse>
    public suspend fun removeRatings(request: SimklRemoveRatingsRequest): ApiResponse<SimklRemoveRatingsResponse>
    public suspend fun getUserShowRatings(): ApiResponse<SimklUserRatingsResponse>
    public suspend fun getShowSummary(simklId: Long): ApiResponse<SimklShowSummaryResponse>
}
