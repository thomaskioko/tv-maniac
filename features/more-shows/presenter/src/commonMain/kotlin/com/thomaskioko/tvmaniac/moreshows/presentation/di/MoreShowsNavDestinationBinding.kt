package com.thomaskioko.tvmaniac.moreshows.presentation.di

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
public interface MoreShowsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideMoreShowsNavDestination(
            graphFactory: MoreShowsScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.MoreShows

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild {
                val moreConfig = config as RootDestinationConfig.MoreShows
                val graph = graphFactory.createMoreShowsGraph(componentContext)
                return ScreenDestination(graph.moreShowsFactory.create(moreConfig.id))
            }
        }
    }
}
