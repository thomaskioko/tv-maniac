package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultContinueWatchingRepository(
    private val continueWatchingStore: ContinueWatchingStore,
) : ContinueWatchingRepository {

    override suspend fun sync(forceRefresh: Boolean, useNitro: Boolean) {
        val key = if (useNitro) ContinueWatchingKey.Nitro else ContinueWatchingKey.Progress
        try {
            continueWatchingStore.fetchWith(key, forceRefresh)
        } catch (_: FetcherSkipSignal) {
            // Intentional: fetcher signaled "leave the local table alone".
            // Empty Nitro response within the guard window, or upstream HTTP failure
            // on the documented multi-step path. The DAO state is preserved.
        }
    }
}
