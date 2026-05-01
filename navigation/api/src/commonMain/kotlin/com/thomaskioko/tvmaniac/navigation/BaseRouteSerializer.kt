package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * Polymorphic [KSerializer] covering every [BaseRoute] subclass registered via [NavRouteBinding]
 * or [NavRootBinding].
 *
 * Built in `navigation/implementation` from both multibinding sets and consumed by Decompose's
 * per-tab `childStack(serializer = ...)`. Each tab's back stack starts with a [NavRoot] at the
 * bottom and accumulates [NavRoute] entries on top, so the serializer must accept either subtype.
 * Feature modules never implement this interface directly; they only contribute a binding to one
 * of the two sets.
 */
public interface BaseRouteSerializer {
    /** Aggregated polymorphic serializer. Pass to Decompose's `childStack`. */
    public val serializer: KSerializer<BaseRoute>
}
