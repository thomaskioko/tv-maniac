package com.thomaskioko.tvmaniac.startwatching.testing

import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeStartWatchingRepository : StartWatchingRepository {

    public data class SyncInvocation(val forceRefresh: Boolean)

    private val startWatchingShows = MutableStateFlow<List<StartWatchingShow>>(emptyList())
    private val syncInvocations = mutableListOf<SyncInvocation>()
    private var storedShows: List<StartWatchingShow> = emptyList()
    private var showsWrittenBySync: List<StartWatchingShow>? = null

    public fun setStartWatchingShows(shows: List<StartWatchingShow>) {
        startWatchingShows.value = shows
        storedShows = shows
    }

    public fun setShowsWrittenBySync(shows: List<StartWatchingShow>?) {
        showsWrittenBySync = shows
    }

    public fun syncInvocations(): List<SyncInvocation> = syncInvocations.toList()

    public fun clearInvocations() {
        syncInvocations.clear()
    }

    override fun observeStartWatching(): Flow<List<StartWatchingShow>> = startWatchingShows.asStateFlow()

    override suspend fun getStartWatching(): List<StartWatchingShow> = storedShows

    override suspend fun syncWatchlist(forceRefresh: Boolean) {
        syncInvocations.add(SyncInvocation(forceRefresh = forceRefresh))
        showsWrittenBySync?.let { storedShows = it }
    }
}
