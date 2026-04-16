package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * Polymorphic [KSerializer] covering every [SheetConfig] subclass registered via
 * [SheetConfigBinding].
 *
 * The single implementation is built in `navigation/implementation` from the multibound
 * `Set<SheetConfigBinding<*>>` and consumed by Decompose's `childSlot(serializer = ...)` so that
 * the sheet slot can be saved and restored across configuration changes and process death.
 * Feature modules never implement this interface directly; they only contribute a
 * [SheetConfigBinding].
 */
public interface SheetConfigSerializer {
    /** The aggregated polymorphic serializer. Pass this to Decompose's `childSlot`. */
    public val serializer: KSerializer<SheetConfig>
}
