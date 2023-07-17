package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse

interface TraktStatsRemoteDataSource {

    suspend fun getStats(userId: String): ApiResponse<TraktUserStatsResponse, ErrorResponse>
}
