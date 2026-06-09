package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.db.WatchProviders
import kotlinx.coroutines.flow.Flow

public interface WatchProviderRepository {
    public suspend fun fetchWatchProviders(
        showId: Long,
        forceRefresh: Boolean = false,
    )

    public fun observeWatchProviders(showId: Long): Flow<List<WatchProviders>>
}
