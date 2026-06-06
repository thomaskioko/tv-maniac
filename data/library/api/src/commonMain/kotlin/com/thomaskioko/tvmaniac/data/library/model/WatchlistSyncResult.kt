package com.thomaskioko.tvmaniac.data.library.model

/**
 * Outcome of a watchlist write-back. [notFoundCount] is how many submitted ids the provider did not
 * recognize.
 */
public data class WatchlistSyncResult(
    val notFoundCount: Int,
)
