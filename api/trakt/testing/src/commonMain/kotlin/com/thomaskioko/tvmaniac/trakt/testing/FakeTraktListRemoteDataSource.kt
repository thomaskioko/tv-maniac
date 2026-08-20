package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

public class FakeTraktListRemoteDataSource : TraktListRemoteDataSource {

    private var watchListResponse: ApiResponse<List<TraktFollowedShowResponse>> =
        ApiResponse.Success(emptyList())
    private var addShowsResponse: ApiResponse<TraktAddShowToListResponse>? = null
    private var removeShowsResponse: ApiResponse<TraktAddRemoveShowFromListResponse>? = null
    private val addedShows = mutableListOf<TraktShowIds>()
    private val removedShows = mutableListOf<TraktShowIds>()

    public fun addedShows(): List<TraktShowIds> = addedShows

    public fun removedShows(): List<TraktShowIds> = removedShows

    public fun setWatchList(response: ApiResponse<List<TraktFollowedShowResponse>>) {
        watchListResponse = response
    }

    public fun setAddShowsToWatchList(response: ApiResponse<TraktAddShowToListResponse>) {
        addShowsResponse = response
    }

    public fun setRemoveShowsFromWatchList(response: ApiResponse<TraktAddRemoveShowFromListResponse>) {
        removeShowsResponse = response
    }

    override suspend fun getWatchList(
        sortBy: String,
        sortHow: String,
    ): ApiResponse<List<TraktFollowedShowResponse>> = watchListResponse

    override suspend fun addShowsToWatchList(
        shows: List<TraktShowIds>,
    ): ApiResponse<TraktAddShowToListResponse> {
        addedShows.addAll(shows)
        return addShowsResponse ?: error("FakeTraktListRemoteDataSource: addShowsToWatchList not configured")
    }

    override suspend fun removeShowsFromWatchList(
        shows: List<TraktShowIds>,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> {
        removedShows.addAll(shows)
        return removeShowsResponse ?: error("FakeTraktListRemoteDataSource: removeShowsFromWatchList not configured")
    }

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> =
        error("FakeTraktListRemoteDataSource: getUser not configured")

    override suspend fun getUserList(userId: String): ApiResponse<List<TraktPersonalListsResponse>> =
        error("FakeTraktListRemoteDataSource: getUserList not configured")

    override suspend fun getListItems(
        userSlug: String,
        listId: Long,
    ): ApiResponse<List<TraktListItemResponse>> =
        error("FakeTraktListRemoteDataSource: getListItems not configured")

    override suspend fun createList(
        userSlug: String,
        name: String,
    ): ApiResponse<TraktCreateListResponse> =
        error("FakeTraktListRemoteDataSource: createList not configured")

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        showId: Long,
    ): ApiResponse<TraktAddShowToListResponse> =
        error("FakeTraktListRemoteDataSource: addShowToList not configured")

    override suspend fun removeShowFromList(
        userSlug: String,
        listId: Long,
        showId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> =
        error("FakeTraktListRemoteDataSource: removeShowFromList not configured")
}
