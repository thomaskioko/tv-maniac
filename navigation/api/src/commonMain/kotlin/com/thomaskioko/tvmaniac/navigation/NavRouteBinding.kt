package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * Pairs a [NavRoute] subclass with its generated [KSerializer].
 *
 * Features contribute one entry for each route through `@Provides @IntoSet` in the same dependency
 * injection module as their [NavDestination], typically inside a
 * `@ContributesTo(ActivityScope::class) interface`. Contributions are collected as a
 * `Set<NavRouteBinding<*>>` and folded into the polymorphic [NavRouteSerializer] used by
 * Decompose's `childStack` so stacks survive process death.
 *
 * @param T concrete [NavRoute] subclass paired with its serializer.
 * @property kClass concrete `KClass` of the registered subclass.
 * @property serializer generated `KSerializer` for the registered subclass.
 */
public data class NavRouteBinding<T : NavRoute>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)
