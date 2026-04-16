package com.thomaskioko.tvmaniac.presenter.home.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface HomeNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideHomeNavDestination(
            graphFactory: HomeScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(route: NavRoute): Boolean = route is HomeRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild = ScreenDestination(
                presenter = graphFactory.createHomeGraph(componentContext).homePresenter,
            )
        }

        @Provides
        @IntoSet
        public fun provideHomeRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(HomeRoute::class, HomeRoute.serializer())
    }
}
