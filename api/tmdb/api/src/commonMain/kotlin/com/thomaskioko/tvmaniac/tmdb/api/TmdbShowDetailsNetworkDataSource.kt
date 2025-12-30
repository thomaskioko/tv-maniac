package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult

public interface TmdbShowDetailsNetworkDataSource {

    /**
     * Get the primary TV show details by id.
     *
     * @param id TV show id
     */
    public suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse>

    /**
     * Get the similar TV shows.
     *
     * @param id TV show id
     * @param page Page number
     */
    public suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult>

    /**
     * Get TV shows recommendations
     *
     * @param id TV show id
     * @param page Page number
     */
    public suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult>

    /**
     * Returns a list of the watch provider (OTT/streaming) data we have available for TV series.
     *
     * @param id TV show id
     */
    public suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult>
}
