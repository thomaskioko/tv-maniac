package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultContinueWatchingRepository(
    private val continueWatchingStore: ContinueWatchingStore,
) : ContinueWatchingRepository {

    override suspend fun sync(forceRefresh: Boolean) {
        if (forceRefresh) {
            continueWatchingStore.fresh(key = Unit)
        } else {
            continueWatchingStore.get(key = Unit)
        }
    }
}
