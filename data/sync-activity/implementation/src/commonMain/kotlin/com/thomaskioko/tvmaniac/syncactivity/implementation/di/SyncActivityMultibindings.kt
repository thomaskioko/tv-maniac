package com.thomaskioko.tvmaniac.syncactivity.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.syncactivity.api.RemoteActivitySource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface SyncActivityMultibindings {

    @Multibinds(allowEmpty = true)
    public fun remoteActivitySources(): Set<RemoteActivitySource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveRemoteActivitySourceBindingContainer {
    @Provides
    public fun activeRemoteActivitySource(
        sources: Set<RemoteActivitySource>,
        accountManager: AccountManager,
    ): RemoteActivitySource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
