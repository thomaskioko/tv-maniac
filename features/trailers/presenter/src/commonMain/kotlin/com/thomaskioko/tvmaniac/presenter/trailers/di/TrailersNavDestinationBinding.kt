package com.thomaskioko.tvmaniac.presenter.trailers.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
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
            override fun matches(route: NavRoute): Boolean = route is TrailersRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild {
                val trailersRoute = route as TrailersRoute
                val graph = graphFactory.createTrailersGraph(componentContext)
                return ScreenDestination(graph.trailersFactory.create(trailersRoute.id))
            }
        }

        @Provides
        @IntoSet
        public fun provideTrailersRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(TrailersRoute::class, TrailersRoute.serializer())
    }
}
