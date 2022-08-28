package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
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
        listId: Long,
        tmdbShowId: Long
    ): TraktAddShowToListResponse

    suspend fun deleteShowFromList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): TraktAddRemoveShowFromListResponse

}