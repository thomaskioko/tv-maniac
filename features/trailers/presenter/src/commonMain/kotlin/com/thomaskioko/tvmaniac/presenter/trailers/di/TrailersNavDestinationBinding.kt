package com.thomaskioko.tvmaniac.presenter.trailers.di

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
public interface TrailersNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideTrailersNavDestination(
            graphFactory: TrailersScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.Trailers

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild {
                val trailersConfig = config as RootDestinationConfig.Trailers
                val graph = graphFactory.createTrailersGraph(componentContext)
                return ScreenDestination(graph.trailersFactory.create(trailersConfig.id))
            }
        }
    }
}
