package com.thomaskioko.tvmaniac.discover.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(DiscoverRoot::class)
public interface DiscoverShowsTabGraph {
    public val discoverShowsPresenter: DiscoverShowsPresenter

    @ContributesTo(HomeRoute::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createDiscoverShowsTabGraph(@Provides componentContext: ComponentContext): DiscoverShowsTabGraph
    }
}
