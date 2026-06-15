package com.thomaskioko.tvmaniac.tmdb.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult

public class FakeTmdbShowDetailsNetworkDataSource(
    private var recommendedShowsResponse: ApiResponse<TmdbShowResult> =
        ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "FakeTmdbShowDetailsNetworkDataSource: getRecommendedShows not configured"),
) : TmdbShowDetailsNetworkDataSource {

    private val showDetailsResponses = mutableMapOf<Long, ApiResponse<TmdbShowDetailsResponse>>()
    private var defaultDetailsResponse: ApiResponse<TmdbShowDetailsResponse> =
        ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "FakeTmdbShowDetailsNetworkDataSource: not configured")

    public fun setShowDetails(id: Long, response: ApiResponse<TmdbShowDetailsResponse>) {
        showDetailsResponses[id] = response
    }

    public fun setDefaultShowDetails(response: ApiResponse<TmdbShowDetailsResponse>) {
        defaultDetailsResponse = response
    }

    public fun setRecommendedShows(response: ApiResponse<TmdbShowResult>) {
        recommendedShowsResponse = response
    }

    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> =
        showDetailsResponses[id] ?: defaultDetailsResponse

    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowDetailsNetworkDataSource: getSimilarShows not configured")

    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> =
        recommendedShowsResponse

    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> =
        error("FakeTmdbShowDetailsNetworkDataSource: getShowWatchProviders not configured")
}
