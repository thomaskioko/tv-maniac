package com.thomaskioko.tvmaniac.settings.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface SettingsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideSettingsNavDestination(
            graphFactory: SettingsScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(route: NavRoute): Boolean = route is SettingsRoute

            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild = ScreenDestination(
                presenter = graphFactory.createSettingsGraph(componentContext).settingsPresenter,
            )
        }

        @Provides
        @IntoSet
        public fun provideSettingsRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(SettingsRoute::class, SettingsRoute.serializer())
    }
}
