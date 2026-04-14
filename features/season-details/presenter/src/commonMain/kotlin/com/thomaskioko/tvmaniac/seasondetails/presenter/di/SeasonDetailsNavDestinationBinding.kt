package com.thomaskioko.tvmaniac.seasondetails.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
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
            override fun matches(route: NavRoute): Boolean = route is SeasonDetailsRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild {
                val seasonRoute = route as SeasonDetailsRoute
                val graph = graphFactory.createSeasonDetailsGraph(componentContext)
                return ScreenDestination(graph.seasonDetailsFactory.create(seasonRoute.param))
            }
        }

        @Provides
        @IntoSet
        public fun provideSeasonDetailsRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(SeasonDetailsRoute::class, SeasonDetailsRoute.serializer())
    }
}
