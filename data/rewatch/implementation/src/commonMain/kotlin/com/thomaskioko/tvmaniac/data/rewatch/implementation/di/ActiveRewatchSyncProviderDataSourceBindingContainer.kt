package com.thomaskioko.tvmaniac.data.rewatch.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSyncProviderDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveRewatchSyncProviderDataSourceBindingContainer {
    @Provides
    public fun activeRewatchSyncProviderDataSource(
        sources: Set<RewatchSyncProviderDataSource>,
        accountManager: AccountManager,
    ): RewatchSyncProviderDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
