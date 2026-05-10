package com.thomaskioko.tvmaniac.genreshows.presenter.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsDestination
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface GenreShowsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideGenreShowsNavDestination(): NavDestination<*> = NavDestination.Screen(
            routeClass = GenreShowsRoute::class,
        ) { _, _ -> GenreShowsDestination }

        @Provides
        @IntoSet
        public fun provideGenreShowsRouteBinding(): NavRouteBinding<*> =
            NavRouteBinding(GenreShowsRoute::class, GenreShowsRoute.serializer())
    }
}
