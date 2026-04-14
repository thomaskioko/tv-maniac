package com.thomaskioko.tvmaniac.presenter.showdetails.di

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
public interface ShowDetailsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideShowDetailsNavDestination(
            graphFactory: ShowDetailsScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.ShowDetails

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild {
                val showConfig = config as RootDestinationConfig.ShowDetails
                val graph = graphFactory.createShowDetailsGraph(componentContext)
                return ScreenDestination(graph.showDetailsFactory.create(showConfig.param))
            }
        }
    }
}
