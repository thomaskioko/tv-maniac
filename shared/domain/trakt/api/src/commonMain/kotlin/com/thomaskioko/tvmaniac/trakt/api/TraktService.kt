package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse

private const val DEFAULT_API_PAGE = 1

interface TraktService {

    suspend fun getAccessToken(authCode: String): TraktAccessTokenResponse

    suspend fun getAccessRefreshToken(refreshToken: String): TraktAccessRefreshTokenResponse

    suspend fun revokeAccessToken(authCode: String)

    suspend fun getUserProfile(userId: String): ApiResponse<TraktUserResponse, ErrorResponse>

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>

    suspend fun getUserStats(userId: String): TraktUserStatsResponse

    suspend fun createFollowingList(userSlug: String): TraktCreateListResponse

    suspend fun getFollowedList(listId: Int, userSlug: String): List<TraktFollowedShowResponse>

    suspend fun getWatchList(): List<TraktFollowedShowResponse>

    suspend fun addShowToWatchList(showId: Int): TraktAddShowToListResponse

    suspend fun removeShowFromWatchList(showId: Int): TraktAddRemoveShowFromListResponse

    suspend fun addShowToList(
        userSlug: String,
        listId: Int,
        traktShowId: Int
    ): TraktAddShowToListResponse

    suspend fun deleteShowFromList(
        userSlug: String,
        listId: Int,
        traktShowId: Int
    ): TraktAddRemoveShowFromListResponse

    suspend fun getTrendingShows(page: Int = DEFAULT_API_PAGE): ApiResponse<List<TraktShowsResponse>, ErrorResponse>

    suspend fun getRecommendedShows(
        page: Int = DEFAULT_API_PAGE,
        period: String
    ): ApiResponse<List<TraktShowsResponse>, ErrorResponse>

    suspend fun getAnticipatedShows(page: Int = DEFAULT_API_PAGE): ApiResponse<List<TraktShowsResponse>, ErrorResponse>

    suspend fun getPopularShows(page: Int = DEFAULT_API_PAGE): ApiResponse<List<TraktShowResponse>, ErrorResponse>

    suspend fun getSimilarShows(traktId: Int): List<TraktShowResponse>

    suspend fun getShowSeasons(traktId: Int): List<TraktSeasonsResponse>

    suspend fun getSeasonWithEpisodes(traktId: Int): List<TraktSeasonEpisodesResponse>

    suspend fun getSeasonEpisodes(traktId: Int): ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse>

    suspend fun getSeasonDetails(traktId: Int): ApiResponse<TraktShowResponse, ErrorResponse>
}