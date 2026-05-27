package com.thomaskioko.tvmaniac.continuewatching.testing

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository

public class FakeContinueWatchingRepository : ContinueWatchingRepository {

    public data class SyncInvocation(
        val forceRefresh: Boolean,
        val useNitro: Boolean,
    )

    private val syncInvocations = mutableListOf<SyncInvocation>()
    private var entries: List<ContinueWatchingEntry> = emptyList()

    public fun syncInvocations(): List<SyncInvocation> = syncInvocations.toList()

    public fun clearInvocations() {
        syncInvocations.clear()
    }

    public fun setEntries(entries: List<ContinueWatchingEntry>) {
        this.entries = entries
    }

    override suspend fun sync(forceRefresh: Boolean, useNitro: Boolean) {
        syncInvocations.add(SyncInvocation(forceRefresh = forceRefresh, useNitro = useNitro))
    }

    override suspend fun getEntries(): List<ContinueWatchingEntry> = entries
}
