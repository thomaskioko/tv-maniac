package com.thomaskioko.tvmaniac.data.library.testing

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistShowIds
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult

public class FakeLibraryRemoteDataSource : LibraryRemoteDataSource {

    override var provider: SyncProviderSource = SyncProviderSource.TRAKT

    private var watchlistResponse: ApiResponse<List<RemoteFollowedShow>> = ApiResponse.Success(emptyList())
    private var addResponse: ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))
    private var removeResponse: ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))
    private val addedShows = mutableListOf<List<WatchlistShowIds>>()
    private val removedShows = mutableListOf<List<WatchlistShowIds>>()

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

    public fun addedShows(): List<List<WatchlistShowIds>> = addedShows

    public fun removedShows(): List<List<WatchlistShowIds>> = removedShows

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> =
        watchlistResponse

    override suspend fun addToWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult> {
        addedShows.add(shows)
        return addResponse
    }

    override suspend fun removeFromWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult> {
        removedShows.add(shows)
        return removeResponse
    }
}
