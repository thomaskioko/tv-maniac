package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.db.WatchProviders
import kotlinx.coroutines.flow.Flow

public interface WatchProviderRepository {
    public suspend fun fetchWatchProviders(
        id: Long,
        forceRefresh: Boolean = false,
    )

    public fun observeWatchProviders(id: Long): Flow<List<WatchProviders>>
}
