package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultContinueWatchingRepository(
    private val nitroStore: NitroContinueWatchingStore,
    private val progressStore: ProgressContinueWatchingStore,
) : ContinueWatchingRepository {

    override suspend fun sync(forceRefresh: Boolean, useNitro: Boolean) {
        try {
            if (useNitro) {
                nitroStore.fetchWith(forceRefresh)
            } else {
                progressStore.fetchWith(forceRefresh)
            }
        } catch (_: FetcherSkipSignal) {
            // Intentional: a store signaled "leave the local table alone".
            // Upstream HTTP failure, or Nitro's empty-response guard. The DAO state is preserved.
        }
    }
}
