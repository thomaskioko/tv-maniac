package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(ActivityScope::class)
public interface NavigationMultibindings {
    @Multibinds
    public fun navDestinations(): Set<NavDestination>

    @Multibinds
    public fun navRouteBindings(): Set<NavRouteBinding<*>>

    @Multibinds(allowEmpty = true)
    public fun navRoots(): Set<NavRoot>

    @Multibinds(allowEmpty = true)
    public fun navRootBindings(): Set<NavRootBinding<*>>
}
