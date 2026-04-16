package com.thomaskioko.tvmaniac.discover.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeScreenScope::class)
public interface DiscoverTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideDiscoverTabDestination(
            graphFactory: DiscoverTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(config: HomeConfig): Boolean =
                config is HomeConfig.Discover

            override fun createChild(
                config: HomeConfig,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createDiscoverTabGraph(componentContext).discoverPresenter,
            )
        }
    }
}
