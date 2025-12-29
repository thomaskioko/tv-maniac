package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

public interface TraktListRemoteDataSource {

    public suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    public suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>

    public suspend fun createFollowingList(userSlug: String): TraktCreateListResponse

    public suspend fun getFollowedList(listId: Long, userSlug: String): List<TraktFollowedShowResponse>

    public suspend fun getWatchList(): List<TraktFollowedShowResponse>

    public suspend fun addShowToWatchList(showId: Long): TraktAddShowToListResponse

    public suspend fun removeShowFromWatchList(showId: Long): TraktAddRemoveShowFromListResponse

    public suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): TraktAddShowToListResponse
}
