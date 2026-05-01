package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * Polymorphic [KSerializer] covering every [NavRoot] subclass registered via [NavRootBinding].
 *
 * Built in `navigation/implementation` from the multibound `Set<NavRootBinding<*>>` and consumed
 * by Decompose's top-level `childStack(serializer = ...)` so the active-tab order survives
 * configuration change and process death. Feature modules never implement this interface
 * directly; they only contribute a [NavRootBinding].
 */
public interface NavRootSerializer {
    /** Aggregated polymorphic serializer. Pass to Decompose's `childStack`. */
    public val serializer: KSerializer<NavRoot>
}
