package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse

private const val DEFAULT_API_PAGE: Long = 1
private const val FETCH_PERIOD: String = "daily"

interface TraktShowsRemoteDataSource {

    suspend fun getTrendingShows(
        page: Long = DEFAULT_API_PAGE,
    ): ApiResponse<List<TraktShowsResponse>>

    suspend fun getRecommendedShows(
        page: Long = DEFAULT_API_PAGE,
        period: String = FETCH_PERIOD,
    ): ApiResponse<List<TraktShowsResponse>>

    suspend fun getAnticipatedShows(
        page: Long = DEFAULT_API_PAGE,
    ): ApiResponse<List<TraktShowsResponse>>

    suspend fun getPopularShows(
        page: Long = DEFAULT_API_PAGE,
    ): ApiResponse<List<TraktShowResponse>>

    suspend fun getSimilarShows(traktId: Long): ApiResponse<List<TraktShowResponse>>

    suspend fun getShowSeasons(traktId: Long): ApiResponse<List<TraktSeasonsResponse>>

    suspend fun getSeasonEpisodes(
        traktId: Long,
    ): ApiResponse<List<TraktSeasonEpisodesResponse>>

    suspend fun getSeasonDetails(traktId: Long): ApiResponse<TraktShowResponse>
}
