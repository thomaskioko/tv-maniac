package com.thomaskioko.tvmaniac.search.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.search.api.SearchRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface SearchMultibindings {

    @Multibinds(allowEmpty = true)
    public fun searchRemoteDataSources(): Set<SearchRemoteDataSource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveSearchRemoteDataSourceBindingContainer {
    @Provides
    public fun activeSearchRemoteDataSource(
        sources: Set<SearchRemoteDataSource>,
        accountManager: AccountManager,
    ): SearchRemoteDataSource =
        sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
            ?: sources.first { it.provider == SyncProviderSource.TRAKT }
}
