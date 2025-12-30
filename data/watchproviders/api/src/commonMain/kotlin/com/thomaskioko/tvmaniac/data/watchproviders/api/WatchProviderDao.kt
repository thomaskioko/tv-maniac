package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.Watch_providers
import kotlinx.coroutines.flow.Flow

public interface WatchProviderDao {
    public fun upsert(entity: Watch_providers)

    public fun fetchWatchProviders(id: Long): List<WatchProviders>

    public fun observeWatchProviders(id: Long): Flow<List<WatchProviders>>

    public fun delete(id: Long)

    public fun deleteAll()
}
