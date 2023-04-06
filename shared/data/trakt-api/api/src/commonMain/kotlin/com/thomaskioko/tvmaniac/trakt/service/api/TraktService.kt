package com.thomaskioko.tvmaniac.trakt.service.api

import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktUserStatsResponse


private const val DEFAULT_API_PAGE: Long = 1

interface TraktService {

    suspend fun getAccessToken(authCode: String): TraktAccessTokenResponse

    suspend fun getAccessRefreshToken(refreshToken: String): TraktAccessRefreshTokenResponse

    suspend fun revokeAccessToken(authCode: String)

    suspend fun getUserProfile(userId: String): ApiResponse<TraktUserResponse, ErrorResponse>

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>

    suspend fun getUserStats(userId: String): TraktUserStatsResponse

    suspend fun createFollowingList(userSlug: String): TraktCreateListResponse

    suspend fun getFollowedList(listId: Long, userSlug: String): List<TraktFollowedShowResponse>

    suspend fun getWatchList(): List<TraktFollowedShowResponse>

    suspend fun addShowToWatchList(showId: Long): TraktAddShowToListResponse

    suspend fun removeShowFromWatchList(showId: Long): TraktAddRemoveShowFromListResponse

    suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long
    ): TraktAddShowToListResponse

    suspend fun deleteShowFromList(
        userSlug: String,
        listId: Long,
        traktShowId: Long
    ): TraktAddRemoveShowFromListResponse

    suspend fun getTrendingShows(page: Long = DEFAULT_API_PAGE): ApiResponse<List<TraktShowsResponse>, ErrorResponse>

    suspend fun getRecommendedShows(
        page: Long = DEFAULT_API_PAGE,
        period: String
    ): ApiResponse<List<TraktShowsResponse>, ErrorResponse>

    suspend fun getAnticipatedShows(page: Long = DEFAULT_API_PAGE): ApiResponse<List<TraktShowsResponse>, ErrorResponse>

    suspend fun getPopularShows(page: Long = DEFAULT_API_PAGE): ApiResponse<List<TraktShowResponse>, ErrorResponse>

    suspend fun getSimilarShows(traktId: Long): ApiResponse<List<TraktShowResponse>, ErrorResponse>

    suspend fun getShowSeasons(traktId: Long): ApiResponse<List<TraktSeasonsResponse>, ErrorResponse>

    suspend fun getSeasonEpisodes(traktId: Long): ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse>

    suspend fun getSeasonDetails(traktId: Long): ApiResponse<TraktShowResponse, ErrorResponse>
}