package com.thomaskioko.tvmaniac.watchlist.implementation.di

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.watchlist.implementation.WatchlistSyncer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface WatchlistSyncerModule {
    @Provides
    @IntoSet
    fun provideWatchlistSyncer(impl: WatchlistSyncer): AppInitializer = impl
}
