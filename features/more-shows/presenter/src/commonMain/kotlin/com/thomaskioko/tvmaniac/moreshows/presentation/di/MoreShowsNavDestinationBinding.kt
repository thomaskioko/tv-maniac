package com.thomaskioko.tvmaniac.moreshows.presentation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
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
            override fun matches(route: NavRoute): Boolean = route is MoreShowsRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild {
                val moreRoute = route as MoreShowsRoute
                val graph = graphFactory.createMoreShowsGraph(componentContext)
                return ScreenDestination(graph.moreShowsFactory.create(moreRoute.id))
            }
        }

        @Provides
        @IntoSet
        public fun provideMoreShowsRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(MoreShowsRoute::class, MoreShowsRoute.serializer())
    }
}
