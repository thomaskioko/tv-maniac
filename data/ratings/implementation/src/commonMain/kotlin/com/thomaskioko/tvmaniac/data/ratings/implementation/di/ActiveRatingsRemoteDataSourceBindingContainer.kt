package com.thomaskioko.tvmaniac.data.ratings.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveRatingsRemoteDataSourceBindingContainer {
    @Provides
    public fun activeRatingsRemoteDataSource(
        sources: Set<RatingsRemoteDataSource>,
        accountManager: AccountManager,
    ): RatingsRemoteDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
