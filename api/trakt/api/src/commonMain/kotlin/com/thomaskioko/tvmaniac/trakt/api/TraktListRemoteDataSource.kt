package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

interface TraktListRemoteDataSource {

    suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>

    suspend fun createFollowingList(userSlug: String): TraktCreateListResponse

    suspend fun getFollowedList(listId: Long, userSlug: String): List<TraktFollowedShowResponse>

    suspend fun getWatchList(): List<TraktFollowedShowResponse>

    suspend fun addShowToWatchList(showId: Long): TraktAddShowToListResponse

    suspend fun removeShowFromWatchList(showId: Long): TraktAddRemoveShowFromListResponse

    suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): TraktAddShowToListResponse
}
