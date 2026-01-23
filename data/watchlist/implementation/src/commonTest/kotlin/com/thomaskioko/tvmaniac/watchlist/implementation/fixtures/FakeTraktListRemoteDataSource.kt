package com.thomaskioko.tvmaniac.watchlist.implementation.fixtures

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.IdsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddedShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktDeletedShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktExistingShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNotFoundShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

internal class FakeTraktListRemoteDataSource : TraktListRemoteDataSource {
    var watchlistResponse: ApiResponse<List<TraktFollowedShowResponse>> =
        ApiResponse.Success(emptyList())
    var addShowsCallCount = 0
    var removeShowsCallCount = 0
    var lastAddedTraktIds: List<Long> = emptyList()
    var lastRemovedTraktIds: List<Long> = emptyList()

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> {
        throw NotImplementedError()
    }

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> {
        throw NotImplementedError()
    }

    override suspend fun createFollowingList(userSlug: String): TraktCreateListResponse {
        throw NotImplementedError()
    }

    override suspend fun getFollowedList(
        listId: Long,
        userSlug: String,
    ): List<TraktFollowedShowResponse> {
        throw NotImplementedError()
    }

    override suspend fun getWatchList(sortBy: String): ApiResponse<List<TraktFollowedShowResponse>> {
        return watchlistResponse
    }

    override suspend fun addShowToWatchListByTmdbId(tmdbId: Long): ApiResponse<TraktAddShowToListResponse> {
        throw NotImplementedError()
    }

    override suspend fun removeShowFromWatchListByTmdbId(tmdbId: Long): ApiResponse<TraktAddRemoveShowFromListResponse> {
        throw NotImplementedError()
    }

    override suspend fun addShowToWatchListByTraktId(traktId: Long): ApiResponse<TraktAddShowToListResponse> {
        addShowsCallCount++
        lastAddedTraktIds = lastAddedTraktIds + traktId
        return ApiResponse.Success(
            TraktAddShowToListResponse(
                added = TraktAddedShowsResponse(shows = 1),
                existing = TraktExistingShowsResponse(shows = 0),
                notFound = TraktNotFoundShowsResponse(shows = emptyList()),
                list = TraktListResponse(itemCount = 1, updateAdd = "2024-01-01T00:00:00.000Z"),
            ),
        )
    }

    override suspend fun removeShowFromWatchListByTraktId(traktId: Long): ApiResponse<TraktAddRemoveShowFromListResponse> {
        removeShowsCallCount++
        lastRemovedTraktIds = lastRemovedTraktIds + traktId
        return ApiResponse.Success(
            TraktAddRemoveShowFromListResponse(
                deleted = TraktDeletedShowsResponse(shows = 1),
                notFound = TraktNotFoundShowsResponse(shows = emptyList()),
                list = TraktListResponse(itemCount = 0, updateAdd = "2024-01-01T00:00:00.000Z"),
            ),
        )
    }

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): TraktAddShowToListResponse {
        throw NotImplementedError()
    }

    companion object {
        fun createTraktFollowedShowResponse(
            traktId: Long,
            tmdbId: Long? = null,
            listedAt: String = "2024-01-01T00:00:00.000Z",
        ): TraktFollowedShowResponse = TraktFollowedShowResponse(
            rank = 1,
            id = traktId.toInt(),
            listedAt = listedAt,
            type = "show",
            show = ShowResponse(
                title = "Test Show $traktId",
                year = 2023,
                ids = IdsResponse(
                    trakt = traktId,
                    slug = "test-show-$traktId",
                    imdb = "tt$traktId",
                    tmdb = tmdbId ?: 0L,
                    tvdb = tmdbId,
                ),
            ),
        )
    }
}
