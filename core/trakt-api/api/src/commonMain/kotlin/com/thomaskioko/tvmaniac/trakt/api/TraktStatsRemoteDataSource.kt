package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse

interface TraktStatsRemoteDataSource {

    suspend fun getStats(userId: String): ApiResponse<TraktUserStatsResponse>
}
