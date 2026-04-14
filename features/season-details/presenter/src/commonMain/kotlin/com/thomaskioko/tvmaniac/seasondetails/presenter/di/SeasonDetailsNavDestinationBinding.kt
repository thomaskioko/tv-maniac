package com.thomaskioko.tvmaniac.seasondetails.presenter.di

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
public interface SeasonDetailsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideSeasonDetailsNavDestination(
            graphFactory: SeasonDetailsScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.SeasonDetails

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild {
                val seasonConfig = config as RootDestinationConfig.SeasonDetails
                val graph = graphFactory.createSeasonDetailsGraph(componentContext)
                return ScreenDestination(graph.seasonDetailsFactory.create(seasonConfig.param))
            }
        }
    }
}
