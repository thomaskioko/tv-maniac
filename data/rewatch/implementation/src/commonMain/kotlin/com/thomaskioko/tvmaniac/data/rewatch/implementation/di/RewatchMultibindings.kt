package com.thomaskioko.tvmaniac.data.rewatch.implementation.di

import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSyncProviderDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface RewatchMultibindings {
    @Multibinds(allowEmpty = true)
    public fun rewatchSyncProviderDataSources(): Set<RewatchSyncProviderDataSource>
}
