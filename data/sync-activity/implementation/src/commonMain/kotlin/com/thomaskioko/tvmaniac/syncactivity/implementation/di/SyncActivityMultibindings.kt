package com.thomaskioko.tvmaniac.syncactivity.implementation.di

import com.thomaskioko.tvmaniac.syncactivity.api.RemoteActivitySource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface SyncActivityMultibindings {

    @Multibinds(allowEmpty = true)
    public fun remoteActivitySources(): Set<RemoteActivitySource>
}
