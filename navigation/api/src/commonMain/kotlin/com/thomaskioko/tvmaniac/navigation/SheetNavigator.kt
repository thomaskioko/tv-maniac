package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

/**
 * Owns the single `SlotNavigation` backing the root modal sheet slot.
 *
 * Every sheet-owning feature activates its config through this navigator instead of declaring
 * its own `SlotNavigation`. Sheet activation is one call: callers construct the
 * [SheetConfig] inline at the call site and pass it to [activate]. The render-site presenter
 * (`DefaultRootPresenter`) calls [buildChildSlot] once with its `ComponentContext` and a child
 * factory and exposes the returned read-only [Value] to the UI.
 *
 * The navigator owns the underlying `SlotNavigation` privately, so callers cannot reach the
 * mutation surface beyond [activate] and [dismiss].
 */
public interface SheetNavigator {
    /** Activate [config] in the sheet slot, replacing any currently active sheet. */
    public fun activate(config: SheetConfig)

    /** Dismiss the currently active sheet, if any. */
    public fun dismiss()

    /**
     * Builds the sheet child slot for [componentContext], returning the read-only [Value]
     * consumed by the UI. The serializer, key, and back-button handling are configured inside
     * the navigator default; callers supply only the context and the child factory.
     */
    public fun <T : Any> buildChildSlot(
        componentContext: ComponentContext,
        childFactory: (SheetConfig, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>>
}
