package com.thomaskioko.tvmaniac.navigation

import kotlin.reflect.KClass

/**
 * Event language for the [DefaultNavigator]'s multi-stack navigation source.
 *
 * Public [Navigator] methods translate one call into one [MultiStackNavEvent] and post it to a
 * `SimpleNavigation<MultiStackNavEvent>`. Decompose then runs [multiStackNavTransformer] against
 * the current [MultiStackNavState] to compute the next state. Keeping the event language sealed
 * lets the transformer match exhaustively on each variant and rejects any new event without an
 * accompanying transformer branch.
 */
internal sealed interface MultiStackNavEvent {
    /** Pushes [route] onto the active tab's stack. */
    data class Push(val route: NavRoute) : MultiStackNavEvent

    /** Brings any existing instance of [route]'s class to the top, or pushes [route] if absent. */
    data class BringToFront(val route: NavRoute) : MultiStackNavEvent

    /** Pushes [route] to the top, removing only an entry that equals [route] exactly. */
    data class PushToFront(val route: NavRoute) : MultiStackNavEvent

    /**
     * Pops entries from the top of the active stack. With the default [toIndex] of `-1`, pops one
     * entry. With a non-negative [toIndex], pops until the entry at that index is on top.
     */
    data class Pop(val toIndex: Int = -1) : MultiStackNavEvent

    /**
     * Pops entries from the top of the active stack until the most recent entry of type
     * [routeClass] is on top. If [inclusive] is `true`, that entry is also popped.
     */
    data class PopUntilType(
        val routeClass: KClass<out NavRoute>,
        val inclusive: Boolean,
    ) : MultiStackNavEvent

    /**
     * Switches the active tab to [root]. If [resetStack] is `true`, the target tab's stack is
     * cleared to its root entry.
     */
    data class SwitchTab(val root: NavRoot, val resetStack: Boolean) : MultiStackNavEvent

    /** Resets every tab's stack to its root entry and activates [root]. */
    data class ReplaceAll(val root: NavRoot) : MultiStackNavEvent
}
