package com.thomaskioko.tvmaniac.watchlist.implementation.di

import com.thomaskioko.tvmaniac.core.base.di.AsyncInitializers
import com.thomaskioko.tvmaniac.watchlist.implementation.WatchlistSyncer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface WatchlistSyncerModule {
    @Provides
    @IntoSet
    @AsyncInitializers
    fun provideWatchlistSyncer(bind: WatchlistSyncer): () -> Unit = {
        bind.init()
    }
}
