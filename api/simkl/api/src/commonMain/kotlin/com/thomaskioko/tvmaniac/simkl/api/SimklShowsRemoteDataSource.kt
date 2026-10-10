package com.thomaskioko.tvmaniac.simkl.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSearchShowResponse

public interface SimklShowsRemoteDataSource {
    public suspend fun searchShows(query: String, limit: Int): ApiResponse<List<SimklSearchShowResponse>>
}
