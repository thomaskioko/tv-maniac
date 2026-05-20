package com.thomaskioko.tvmaniac.continuewatching.testing

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository

public class FakeContinueWatchingRepository : ContinueWatchingRepository {

    private val syncInvocations = mutableListOf<Boolean>()

    public fun syncInvocations(): List<Boolean> = syncInvocations.toList()

    public fun clearInvocations() {
        syncInvocations.clear()
    }

    override suspend fun sync(forceRefresh: Boolean) {
        syncInvocations.add(forceRefresh)
    }
}
