package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * Single polymorphic [KSerializer] covering every [NavRoute] subclass registered via
 * [NavRouteBinding] multibinding. Consumed by Decompose's `childStack(serializer = ...)`
 * so state can be saved and restored across configuration changes and process death.
 */
public interface NavRouteSerializer {
    public val serializer: KSerializer<NavRoute>
}
