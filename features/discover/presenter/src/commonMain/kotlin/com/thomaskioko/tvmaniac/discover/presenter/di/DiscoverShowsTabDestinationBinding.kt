package com.thomaskioko.tvmaniac.discover.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeRoute::class)
public interface DiscoverShowsTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideDiscoverShowsTabDestination(
            graphFactory: DiscoverShowsTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(root: NavRoot): Boolean = root is DiscoverRoot

            override fun createChild(
                root: NavRoot,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createDiscoverShowsTabGraph(componentContext).discoverShowsPresenter,
            )
        }
    }
}
