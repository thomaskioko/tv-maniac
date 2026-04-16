package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.slot.SlotNavigation

/**
 * Owns the single [SlotNavigation] backing the root modal sheet slot.
 *
 * Every sheet-owning feature activates its config through this navigator instead of declaring its
 * own `SlotNavigation`. The root presenter consumes [getSlotNavigation] when building the
 * `childSlot`, so adding a new sheet never touches root. Feature-specific navigators
 * (e.g. `EpisodeSheetNavigator`) delegate to this interface.
 */
public interface SheetNavigator {
    /** Activate [config] in the sheet slot, replacing any currently active sheet. */
    public fun activate(config: SheetConfig)

    /** Dismiss the currently active sheet, if any. */
    public fun dismiss()

    /**
     * Underlying Decompose [SlotNavigation]. Used by the root presenter when building the
     * `childSlot`; not called from feature code.
     */
    public fun getSlotNavigation(): SlotNavigation<SheetConfig>
}
