package com.thomaskioko.tvmaniac.presenter.showdetails.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
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
            override fun matches(route: NavRoute): Boolean = route is ShowDetailsRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild {
                val showRoute = route as ShowDetailsRoute
                val graph = graphFactory.createShowDetailsGraph(componentContext)
                return ScreenDestination(graph.showDetailsFactory.create(showRoute.param))
            }
        }

        @Provides
        @IntoSet
        public fun provideShowDetailsRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer())
    }
}
