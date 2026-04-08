package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

public interface TraktListRemoteDataSource {

    public suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    public suspend fun getUserList(userId: String): ApiResponse<List<TraktPersonalListsResponse>>

    public suspend fun createList(userSlug: String, name: String): ApiResponse<TraktCreateListResponse>

    public suspend fun getWatchList(sortBy: String, sortHow: String): ApiResponse<List<TraktFollowedShowResponse>>

    public suspend fun addShowToWatchListByTmdbId(tmdbId: Long): ApiResponse<TraktAddShowToListResponse>

    public suspend fun removeShowFromWatchListByTmdbId(tmdbId: Long): ApiResponse<TraktAddRemoveShowFromListResponse>

    public suspend fun addShowToWatchListByTraktId(traktId: Long): ApiResponse<TraktAddShowToListResponse>

    public suspend fun removeShowFromWatchListByTraktId(traktId: Long): ApiResponse<TraktAddRemoveShowFromListResponse>

    public suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): ApiResponse<TraktAddShowToListResponse>

    public suspend fun removeShowFromList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse>
}
