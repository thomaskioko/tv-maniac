package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.SheetChildFactory
import com.thomaskioko.tvmaniac.navigation.SheetConfigBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(ActivityScope::class)
public interface NavigationMultibindings {
    @Multibinds
    public fun navDestinations(): Set<NavDestination>

    @Multibinds
    public fun navRouteBindings(): Set<NavRouteBinding<*>>

    @Multibinds
    public fun sheetChildFactories(): Set<SheetChildFactory>

    @Multibinds
    public fun sheetConfigBindings(): Set<SheetConfigBinding<*>>
}
