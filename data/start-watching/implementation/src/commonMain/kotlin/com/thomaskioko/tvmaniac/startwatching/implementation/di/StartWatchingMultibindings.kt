package com.thomaskioko.tvmaniac.startwatching.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface StartWatchingMultibindings {

    @Multibinds(allowEmpty = true)
    public fun startWatchingRemoteDataSources(): Set<StartWatchingRemoteDataSource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveStartWatchingRemoteDataSourceBindingContainer {
    @Provides
    public fun activeStartWatchingRemoteDataSource(
        sources: Set<StartWatchingRemoteDataSource>,
        accountManager: AccountManager,
    ): StartWatchingRemoteDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
