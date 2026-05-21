package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultContinueWatchingRepository(
    private val continueWatchingStore: ContinueWatchingStore,
    private val discoveryStore: ContinueWatchingDiscoveryStore,
) : ContinueWatchingRepository {

    override suspend fun sync(forceRefresh: Boolean, useNitro: Boolean) {
        val key = if (useNitro) ContinueWatchingKey.Nitro else ContinueWatchingKey.Progress
        try {
            if (key == ContinueWatchingKey.Progress) {
                // Discovery seeds placeholder rows for new candidates and prunes hidden ones
                // before the detail store fans out per-show progress. Both stores share the
                // continue-watching freshness signal so they skip together when the cache is fresh.
                discoveryStore.fetchWith(forceRefresh)
            }
            continueWatchingStore.fetchWith(key, forceRefresh)
        } catch (_: FetcherSkipSignal) {
            // Intentional: a fetcher signaled "leave the local table alone".
            // Discovery upstream failure, empty Nitro response within the guard window,
            // or per-show fan-out failure on the detail store. The DAO state is preserved.
        }
    }
}
