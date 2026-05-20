package com.thomaskioko.tvmaniac.continuewatching.testing

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository

public class FakeContinueWatchingRepository : ContinueWatchingRepository {

    public data class SyncInvocation(
        val forceRefresh: Boolean,
        val useNitro: Boolean,
    )

    private val syncInvocations = mutableListOf<SyncInvocation>()

    public fun syncInvocations(): List<SyncInvocation> = syncInvocations.toList()

    public fun clearInvocations() {
        syncInvocations.clear()
    }

    override suspend fun sync(forceRefresh: Boolean, useNitro: Boolean) {
        syncInvocations.add(SyncInvocation(forceRefresh = forceRefresh, useNitro = useNitro))
    }
}
