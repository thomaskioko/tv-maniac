package com.thomaskioko.tvmaniac.data.user.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface UserMultibindings {

    @Multibinds(allowEmpty = true)
    public fun userRemoteDataSources(): Set<UserRemoteDataSource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public interface ActiveUserRemoteDataSourceBindingContainer {
    public companion object {
        @Provides
        public fun activeUserRemoteDataSource(
            sources: Set<UserRemoteDataSource>,
            accountManager: AccountManager,
        ): UserRemoteDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
    }
}
