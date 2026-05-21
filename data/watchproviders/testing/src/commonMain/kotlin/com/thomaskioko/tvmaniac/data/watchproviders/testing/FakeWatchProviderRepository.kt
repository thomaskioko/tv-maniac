package com.thomaskioko.tvmaniac.data.watchproviders.testing

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.db.WatchProviders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeWatchProviderRepository : WatchProviderRepository {
    private var watchProvidersResult = MutableStateFlow<List<WatchProviders>>(emptyList())
    private val fetchInvocations = mutableListOf<FetchInvocation>()

    public data class FetchInvocation(val traktId: Long, val forceRefresh: Boolean)

    public suspend fun setWatchProvidersResult(result: List<WatchProviders>) {
        watchProvidersResult.emit(result)
    }

    public fun fetchInvocations(): List<FetchInvocation> = fetchInvocations.toList()

    public fun clearFetchInvocations() {
        fetchInvocations.clear()
    }

    override suspend fun fetchWatchProviders(traktId: Long, forceRefresh: Boolean) {
        fetchInvocations.add(FetchInvocation(traktId, forceRefresh))
    }

    override fun observeWatchProviders(
        traktId: Long,
    ): Flow<List<WatchProviders>> = watchProvidersResult.asStateFlow()
}
