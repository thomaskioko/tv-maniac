package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * Polymorphic [KSerializer] covering every [NavRoute] subclass registered through [NavRouteBinding].
 *
 * Built in `navigation/implementation` from the multibound `Set<NavRouteBinding<*>>` and consumed
 * by Decompose's `childStack(serializer = ...)` so back stacks survive configuration change and
 * process death. Feature modules never implement this interface directly; they only contribute a
 * [NavRouteBinding].
 */
public interface NavRouteSerializer {
    /** Aggregated polymorphic serializer. Pass to Decompose's `childStack`. */
    public val serializer: KSerializer<NavRoute>
}
