package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.Watch_providers
import kotlinx.coroutines.flow.Flow

interface WatchProviderDao {
    fun upsert(entity: Watch_providers)

    fun fetchWatchProviders(id: Long): List<WatchProviders>

    fun observeWatchProviders(id: Long): Flow<List<WatchProviders>>

    fun delete(id: Long)

    fun deleteAll()
}
