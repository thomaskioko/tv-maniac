package com.thomaskioko.tvmaniac.episodes.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface EpisodeWatchesMultibindings {

    @Multibinds(allowEmpty = true)
    public fun episodeWatchesDataSources(): Set<EpisodeWatchesDataSource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveEpisodeWatchesDataSourceBindingContainer {
    @Provides
    public fun activeEpisodeWatchesDataSource(
        sources: Set<EpisodeWatchesDataSource>,
        accountManager: AccountManager,
    ): EpisodeWatchesDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
