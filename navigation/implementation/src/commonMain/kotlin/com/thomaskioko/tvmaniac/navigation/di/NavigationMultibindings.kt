package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

/**
 * Declares the four navigation multibinding sets feature modules contribute into. Contributed at
 * [ActivityScope] so each feature module ships its own entries without touching a central graph.
 *
 * - `Set<NavDestination<*>>`: route-to-presenter resolvers, one for each screen, overlay, or tab root.
 * - `Set<NavRouteBinding<*>>`: polymorphic serializer entries for each [NavRoute] subclass.
 * - `Set<NavRoot>`: the registered tab anchors.
 * - `Set<NavRootBinding<*>>`: polymorphic serializer entries for each [NavRoot] subclass.
 *
 * The first two sets are non-empty by contract; an app with zero navigation destinations would
 * not start. The two root sets allow empty so test graphs can omit roots they do not exercise.
 */
@ContributesTo(ActivityScope::class)
public interface NavigationMultibindings {
    @Multibinds
    public fun navDestinations(): Set<NavDestination<*>>

    @Multibinds
    public fun navRouteBindings(): Set<NavRouteBinding<*>>

    @Multibinds(allowEmpty = true)
    public fun navRoots(): Set<NavRoot>

    @Multibinds(allowEmpty = true)
    public fun navRootBindings(): Set<NavRootBinding<*>>
}
