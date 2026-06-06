package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.WatchProvidersByTraktId
import com.thomaskioko.tvmaniac.db.Watch_providers
import kotlinx.coroutines.flow.Flow

public interface WatchProviderDao {
    public fun upsert(entity: Watch_providers)

    public fun fetchWatchProviders(tmdbId: Long): List<WatchProviders>

    public fun observeWatchProviders(tmdbId: Long): Flow<List<WatchProviders>>

    public fun observeWatchProvidersByTraktId(showId: Long): Flow<List<WatchProvidersByTraktId>>

    public fun deleteByTraktId(showId: Long)

    public fun deleteAll()
}
