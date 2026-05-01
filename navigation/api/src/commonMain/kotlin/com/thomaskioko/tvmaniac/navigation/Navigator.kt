package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlin.reflect.KClass

/**
 * Single entry point for mutating the multi-stack navigation state.
 *
 * Hides three Decompose primitives behind one public surface: a top-level [com.arkivanov.decompose.router.stack.StackNavigation]
 * tracking active-tab order, one [com.arkivanov.decompose.router.stack.StackNavigation] per registered [NavRoot] for the per-tab
 * back stacks, and a [com.arkivanov.decompose.router.slot.SlotNavigation] for overlays. Presenters depend on this interface
 * rather than Decompose directly, so call sites stay focused on intent (navigate to a route, switch a tab) and tests
 * substitute a fake without pulling in Decompose.
 *
 * Method names mirror khonshu's `Navigator` vocabulary. [navigateTo] dispatches by route type:
 * [OverlayRoute] activates the overlay slot; any other [NavRoute] pushes onto the active tab's stack. Tab navigation
 * is [switchBackStack] (preserves the target's stack) or [showRoot] (clears it).
 *
 * Render-site presenters call one of [buildRootStack], [buildOverlaySlot], or [buildTabStack] once with their
 * `ComponentContext` and a child factory, then expose the returned read-only [Value] to the UI.
 */
public interface Navigator {
    /**
     * Pushes [route] on top of the active tab's back stack. If [route] implements [OverlayRoute],
     * activates it in the overlay slot instead.
     */
    public fun navigateTo(route: NavRoute)

    /**
     * Pops the top entry from the active tab's back stack. No-op if only the tab root remains.
     * If an overlay is active, dismisses the overlay first.
     */
    public fun navigateBack()

    /**
     * Pops entries from the top of the active tab's back stack until the most recent entry whose
     * configuration is an instance of [routeClass] is on top. If [inclusive] is `true`, that entry
     * is also popped. No-op if no entry of the given type exists in the active stack.
     */
    public fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean = false)

    /**
     * Pops entries from the top of the active tab's back stack until the entry at [toIndex] is on
     * top. Used by UI hosts (notably SwiftUI's `NavigationStack`) whose path bindings express
     * navigation in terms of stack depth rather than route type.
     */
    public fun popTo(toIndex: Int)

    /**
     * Brings [route] to the top of the active tab's back stack if any instance of the same type is
     * already present (class-based dedupe), otherwise adds it to the top.
     */
    public fun bringToFront(route: NavRoute)

    /**
     * Pushes [route] to the top of the active tab's back stack, removing the existing entry only
     * if it is exactly equal to [route] (equality-based dedupe).
     */
    public fun pushToFront(route: NavRoute)

    /**
     * Switches the active tab to [root], preserving the target tab's existing back stack. If
     * [root] is already at the top, no-op. If [root] has no entry on the root navigation, pushes
     * it. The current tab's back stack is untouched.
     */
    public fun switchBackStack(root: NavRoot)

    /**
     * Switches the active tab to [root] and clears its back stack to the root entry. Use for
     * explicit reset flows: re-tap on the active tab, deeplink entry, profile sign-out.
     */
    public fun showRoot(root: NavRoot)

    /**
     * Resets the entire navigation state. The root navigation collapses to `[root]` and every
     * registered tab's back stack clears to its root entry. Use for sign-out or hard reset flows.
     */
    public fun replaceAllBackStacks(root: NavRoot)

    /**
     * Builds the activity-level child stack of [NavRoot] entries for [componentContext], starting
     * from [initialRoot]. The returned [Value] tracks active-tab order; the UI uses it to know
     * which tab is currently visible. Serializer, key, and back-button handling are configured
     * inside the navigator default; callers supply only the context, the initial root, and the
     * child factory.
     */
    public fun <T : Any> buildRootStack(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (NavRoot, ComponentContext) -> T,
    ): Value<ChildStack<*, T>>

    /**
     * Builds the overlay child slot for [componentContext], returning the read-only [Value]
     * consumed by the UI. Overlays are [NavRoute] subtypes that implement [OverlayRoute];
     * activation flows through [navigateTo] like any other route.
     */
    public fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>>

    /**
     * Builds the per-tab back stack for [root]. Each registered tab calls this once with its own
     * root and child factory; the returned [Value] feeds the tab body in the UI. The stack
     * remains alive across tab switches so the back stack survives.
     *
     * The factory receives [BaseRoute] because each tab's stack starts with [root] (a [NavRoot])
     * at the bottom and accumulates [NavRoute] entries on top. Implementations dispatch by type
     * to render either the tab's home content (when the entry is a [NavRoot]) or a pushed screen
     * (when the entry is a [NavRoute]).
     */
    public fun <T : Any> buildTabStack(
        componentContext: ComponentContext,
        root: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<ChildStack<*, T>>
}

/**
 * Type-safe overload of [Navigator.navigateBackTo]. Pops back to the most recent entry whose
 * configuration is an instance of [T]. Mirrors khonshu's
 * `navigator.navigateBackTo<HomeRoute>(inclusive = true)` shape.
 */
public inline fun <reified T : NavRoute> Navigator.navigateBackTo(inclusive: Boolean = false) {
    navigateBackTo(T::class, inclusive)
}
