package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation

/**
 * Single entry point for mutating the root navigation stack.
 *
 * Presenters and navigators depend on this interface rather than Decompose's [StackNavigation]
 * directly, so their call sites stay focused on intent (push a route, pop to an index) and tests
 * can substitute a fake without pulling in Decompose. The single implementation is bound at
 * activity scope.
 */
public interface Navigator {
    /**
     * Brings [route] to the top of the stack if it is already present, otherwise adds it to the top.
     * Use for tab-like navigation where re-entering a destination should reuse the existing entry.
     */
    public fun bringToFront(route: NavRoute)

    /**
     * Pushes [route] on top of the stack, creating a new entry even if the same route is already present.
     */
    public fun pushNew(route: NavRoute)

    /**
     * Pushes [route] to the top of the stack, reusing the existing entry if one is already present.
     * Equivalent to [bringToFront]; kept as a separate name for call-site readability.
     */
    public fun pushToFront(route: NavRoute)

    /** Pops the top entry from the stack. No-op if only the root entry remains. */
    public fun pop()

    /**
     * Pops entries from the top of the stack until the entry at [toIndex] is on top.
     */
    public fun popTo(toIndex: Int)

    /**
     * Returns the underlying [StackNavigation] for wiring into Decompose's `childStack` builder.
     * Call sites outside of navigation wiring should prefer the typed methods above.
     */
    public fun getStackNavigation(): StackNavigation<NavRoute>
}
