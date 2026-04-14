package com.thomaskioko.tvmaniac.search.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface SearchNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideSearchNavDestination(
            graphFactory: SearchScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.Search

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild = ScreenDestination(
                presenter = graphFactory.createSearchGraph(componentContext).searchPresenter,
            )
        }
    }
}
