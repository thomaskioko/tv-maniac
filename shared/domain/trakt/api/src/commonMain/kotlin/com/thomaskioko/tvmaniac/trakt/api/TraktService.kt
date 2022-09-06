package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

interface TraktService {

    suspend fun getAccessToken(authCode: String): TraktAccessTokenResponse

    suspend fun getAccessRefreshToken(refreshToken: String): TraktAccessRefreshTokenResponse

    suspend fun revokeAccessToken(authCode: String)

    suspend fun getUserProfile(userId: String): TraktUserResponse

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>

    suspend fun createFavoriteList(userSlug: String): TraktCreateListResponse

    suspend fun getFollowedList(listId: Int, userSlug: String): List<TraktFollowedShowResponse>

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

    suspend fun getTrendingShows(page: Int) : List<TraktShowsResponse>

    suspend fun getRecommendedShows(page: Int, period: String) : List<TraktShowsResponse>

    suspend fun getAnticipatedShows(page: Int) : List<TraktShowsResponse>

    suspend fun getSimilarShows(traktId: Int) : List<TraktShowResponse>

    suspend fun getPopularShows(page: Int) : List<TraktShowResponse>

    suspend fun getShowSeasons(traktId: Int) : List<TraktSeasonsResponse>

    suspend fun getSeasonWithEpisodes(traktId: Int) : List<TraktSeasonEpisodesResponse>

    suspend fun getSeasonDetails(traktId: Int) : TraktShowResponse
}