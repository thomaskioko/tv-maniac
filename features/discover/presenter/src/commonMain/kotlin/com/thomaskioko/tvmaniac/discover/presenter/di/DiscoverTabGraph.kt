package com.thomaskioko.tvmaniac.discover.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.nav.DiscoverTabScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(DiscoverTabScope::class)
public interface DiscoverTabGraph {
    public val discoverPresenter: DiscoverShowsPresenter

    @ContributesTo(HomeScreenScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createDiscoverTabGraph(
            @Provides componentContext: ComponentContext,
        ): DiscoverTabGraph
    }
}
