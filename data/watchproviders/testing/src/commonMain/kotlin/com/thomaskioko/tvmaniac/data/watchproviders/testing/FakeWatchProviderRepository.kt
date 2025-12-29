package com.thomaskioko.tvmaniac.data.watchproviders.testing

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.db.WatchProviders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeWatchProviderRepository : WatchProviderRepository {
    private var watchProvidersResult = MutableStateFlow<List<WatchProviders>>(emptyList())

    public suspend fun setWatchProvidersResult(result: List<WatchProviders>) {
        watchProvidersResult.emit(result)
    }

    override suspend fun fetchWatchProviders(id: Long, forceRefresh: Boolean) {
    }

    override fun observeWatchProviders(
        id: Long,
    ): Flow<List<WatchProviders>> = watchProvidersResult.asStateFlow()
}
