package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistShowIds
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult

public interface LibraryRemoteDataSource : SyncProvider {

    public suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>>

    public suspend fun addToWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult>

    public suspend fun removeFromWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult>
}
