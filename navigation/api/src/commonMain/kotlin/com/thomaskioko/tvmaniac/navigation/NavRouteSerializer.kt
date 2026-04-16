package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * Polymorphic [KSerializer] covering every [NavRoute] subclass registered via [NavRouteBinding].
 *
 * The single implementation is built in `navigation/implementation` from the multibound
 * `Set<NavRouteBinding<*>>` and consumed by Decompose's `childStack(serializer = ...)` so that
 * stacks can be saved and restored across configuration changes and process death. Feature
 * modules never implement this interface directly; they only contribute a [NavRouteBinding].
 */
public interface NavRouteSerializer {
    /** The aggregated polymorphic serializer. Pass this to Decompose's `childStack`. */
    public val serializer: KSerializer<NavRoute>
}
