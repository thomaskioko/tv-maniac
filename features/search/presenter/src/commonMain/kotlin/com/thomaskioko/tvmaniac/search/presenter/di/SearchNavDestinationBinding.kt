package com.thomaskioko.tvmaniac.search.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
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
            override fun matches(route: NavRoute): Boolean = route is SearchRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild = ScreenDestination(
                presenter = graphFactory.createSearchGraph(componentContext).searchPresenter,
            )
        }

        @Provides
        @IntoSet
        public fun provideSearchRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(SearchRoute::class, SearchRoute.serializer())
    }
}
