package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * Pairs a [NavRoute] subclass with its generated [KSerializer].
 *
 * Features contribute one entry per route via `@Provides @IntoSet` in the same DI module as their
 * [NavDestination], typically inside a `@ContributesTo(ActivityScope::class) interface`. The
 * contributions are collected as a `Set<NavRouteBinding<*>>` and folded into the polymorphic
 * [NavRouteSerializer] used by Decompose's `childStack` so stacks survive process death.
 */
public data class NavRouteBinding<T : NavRoute>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)
