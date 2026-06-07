package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.connectedaccount.api.ProviderScoped
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult

public interface LibraryRemoteDataSource : ProviderScoped {

    public suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>>

    public suspend fun addToWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult>

    public suspend fun removeFromWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult>
}
