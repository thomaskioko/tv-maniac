package com.thomaskioko.tvmaniac.data.library.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface LibraryMultibindings {

    @Multibinds(allowEmpty = true)
    public fun libraryRemoteDataSources(): Set<LibraryRemoteDataSource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveLibraryRemoteDataSourceBindingContainer {
    @Provides
    public fun activeLibraryRemoteDataSource(
        sources: Set<LibraryRemoteDataSource>,
        accountManager: AccountManager,
    ): LibraryRemoteDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
