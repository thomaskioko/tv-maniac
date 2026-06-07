package com.thomaskioko.tvmaniac.data.library.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult

public class FakeLibraryRemoteDataSource : LibraryRemoteDataSource {

    override var provider: AccountProvider = AccountProvider.TRAKT

    private var watchlistResponse: ApiResponse<List<RemoteFollowedShow>> = ApiResponse.Success(emptyList())
    private var addResponse: ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))
    private var removeResponse: ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))
    private val addedShowIds = mutableListOf<List<Long>>()
    private val removedShowIds = mutableListOf<List<Long>>()

    public fun setWatchlist(shows: List<RemoteFollowedShow>) {
        watchlistResponse = ApiResponse.Success(shows)
    }

    public fun setWatchlist(response: ApiResponse<List<RemoteFollowedShow>>) {
        watchlistResponse = response
    }

    public fun setAddResponse(response: ApiResponse<WatchlistSyncResult>) {
        addResponse = response
    }

    public fun setRemoveResponse(response: ApiResponse<WatchlistSyncResult>) {
        removeResponse = response
    }

    public fun addedShowIds(): List<List<Long>> = addedShowIds

    public fun removedShowIds(): List<List<Long>> = removedShowIds

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> =
        watchlistResponse

    override suspend fun addToWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult> {
        addedShowIds.add(showIds)
        return addResponse
    }

    override suspend fun removeFromWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult> {
        removedShowIds.add(showIds)
        return removeResponse
    }
}
