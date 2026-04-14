package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * Entry pairing a [NavRoute] subclass with its generated [KSerializer].
 * Features contribute one of these via `@Provides @IntoSet` next to their `NavDestination`
 * so the root presenter can build a single polymorphic serializer for Decompose state keeping.
 */
public data class NavRouteBinding<T : NavRoute>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)
