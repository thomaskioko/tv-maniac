package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import kotlin.reflect.KClass

/**
 * Single entry point for mutating the multi-stack navigation state.
 *
 * Wraps Decompose's [com.arkivanov.decompose.router.children.children] navigation API behind one
 * public surface that coordinates a back stack for each tab through a single
 * [com.arkivanov.decompose.router.children.NavState]. A separate
 * [com.arkivanov.decompose.router.slot.SlotNavigation] handles overlays. Presenters depend on this
 * interface rather than Decompose directly, so call sites stay focused on intent (navigate to a
 * route, switch a tab) and tests substitute a fake without pulling in Decompose.
 *
 * [navigateTo] dispatches by route type:
 * [OverlayRoute] activates the overlay slot; any other [NavRoute] pushes onto the active tab's stack. Tab navigation
 * is [switchBackStack] (preserves the target's stack) or [showRoot] (clears it).
 *
 * The host (typically `HomePresenter`) calls [buildHostNavigation] once with its `ComponentContext` and a child
 * factory; the returned [Value] tracks the active root and per-tab [com.arkivanov.decompose.router.stack.ChildStack]s.
 * Presenters that only need the active root may observe [activeRoot] directly. Overlay-rendering hosts call
 * [buildOverlaySlot].
 */
public interface Navigator {
    /**
     * Observable currently active [NavRoot]. Backed by the same state as [buildHostNavigation], so
     * updates emit on every tab switch, [showRoot], and [replaceAllBackStacks].
     */
    public val activeRoot: Value<NavRoot>

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
     * [root] is already active, no-op. The current tab's back stack is untouched.
     */
    public fun switchBackStack(root: NavRoot)

    /**
     * Switches the active tab to [root] and clears its back stack to the root entry. Use for
     * explicit reset flows: re-tap on the active tab, deeplink entry, profile sign-out.
     */
    public fun showRoot(root: NavRoot)

    /**
     * Resets the entire navigation state. Every registered tab's back stack clears to its root
     * entry and [root] becomes active. Use for sign-out or hard reset flows.
     */
    public fun replaceAllBackStacks(root: NavRoot)

    /**
     * Connects the multi-stack navigation host to [componentContext]. Call exactly once per host
     * (typically from `HomePresenter.init`). [initialRoot] is the active tab on first launch; if a
     * previously saved state is present, the saved active root wins. The returned [Value] exposes
     * the current active root and a [com.arkivanov.decompose.router.stack.ChildStack] for each
     * registered [NavRoot]; project tab stacks through `value.tabStacks[root]`.
     *
     * Decompose's `children(...)` API coordinates child lifecycles per
     * [com.arkivanov.decompose.router.children.ChildNavState.Status]: the active tab's top is
     * `RESUMED`, every other entry is `CREATED` (instance preserved, back handlers detached,
     * callbacks paused). The single `backTransformer` registered by this call replaces back-button
     * handlers in each tab so back press always pops the active tab.
     *
     * The [childFactory] receives [BaseRoute] because each tab's stack starts with the tab's
     * [NavRoot] at the bottom and accumulates [NavRoute] entries on top. The factory dispatches
     * by type to render either the tab's home content (when the entry is a [NavRoot]) or a pushed
     * screen (when the entry is a [NavRoute]).
     *
     * @param T component type produced by [childFactory] for each entry.
     * @param componentContext lifecycle owner that scopes the host.
     * @param initialRoot active tab on first launch when no saved state exists.
     * @param childFactory builds the component for each [BaseRoute] entry across every tab.
     * @return read-only [Value] tracking the active root and the back stack for each tab.
     */
    public fun <T : Any> buildHostNavigation(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<MultiStackHostState<T>>

    /**
     * Builds the overlay child slot for [componentContext], returning the read-only [Value]
     * consumed by the UI. Overlays are [NavRoute] subtypes that implement [OverlayRoute];
     * activation flows through [navigateTo] like any other route.
     *
     * @param T component type produced by [childFactory] for the active overlay.
     * @param componentContext lifecycle owner that scopes the slot.
     * @param childFactory builds the component for the active overlay [NavRoute].
     * @return read-only [Value] holding the active overlay child or `null` when none is active.
     */
    public fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>>

    /**
     * Dismisses the active overlay, if any. No-op if no overlay is currently active. Use from a
     * presenter that owns an [OverlayRoute] to close itself programmatically (action commit, deep
     * link, or sibling navigation). Hardware back press already dismisses via the slot's
     * `handleBackButton`.
     */
    public fun dismissOverlay()
}

/**
 * Type-safe overload of [Navigator.navigateBackTo]. Pops back to the most recent entry whose
 * configuration is an instance of [T].
 *
 * @param T target [NavRoute] type to pop back to.
 * @param inclusive when `true`, pops the matching entry as well.
 */
public inline fun <reified T : NavRoute> Navigator.navigateBackTo(inclusive: Boolean = false) {
    navigateBackTo(T::class, inclusive)
}
