package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult

/**
 * Provider-neutral watchlist data source for the active backend.
 *
 * Pulls the followed-show watchlist and flushes pending follow and unfollow write-backs. Each backend
 * supplies an adapter contributed into a multibound set; the consumer selects the one whose [provider]
 * is active.
 */
public interface LibraryRemoteDataSource {

    /** The backend this source talks to; used to select the active source from the multibound set. */
    public val provider: ConnectedProvider

    /** Pulls the followed-show watchlist. */
    public suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>>

    /** Adds [showIds] to the watchlist (a pending follow upload). */
    public suspend fun addToWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult>

    /** Removes [showIds] from the watchlist (a pending unfollow). */
    public suspend fun removeFromWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult>
}
