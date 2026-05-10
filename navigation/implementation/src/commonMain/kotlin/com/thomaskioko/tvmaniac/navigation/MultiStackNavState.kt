package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState

/**
 * Pairs a [NavRoot] with one entry from that tab's back stack so Decompose's `children()` API can
 * address each stack entry uniquely across the multi-stack tree.
 *
 * Decompose requires every [ChildNavState.configuration] in the same tree to be unique. Two tabs
 * may share the same [NavRoot] at the bottom of their stacks, so [tabRoot] is paired with [route]
 * to disambiguate the entry.
 */
internal data class TabbedRoute(
    val tabRoot: NavRoot,
    val route: BaseRoute,
)

/**
 * Snapshot of the multi-stack navigation tree owned by [DefaultNavigator].
 *
 * Each registered [NavRoot] maps to a non-empty list of [BaseRoute] entries: the first element is
 * the tab's root entry and the tail is the pushed [NavRoute] sequence. [activeRoot] points at the
 * tab whose top entry is currently visible. [multiStackNavTransformer] produces new instances of
 * this class in response to a [MultiStackNavEvent].
 *
 * The class is the [NavState] passed to Decompose's `children()`. [children] projects every entry
 * across every tab as a [ChildNavState] with the appropriate lifecycle status.
 */
internal data class MultiStackNavState(
    val activeRoot: NavRoot,
    val tabStacks: Map<NavRoot, List<BaseRoute>>,
) : NavState<TabbedRoute> {
    init {
        require(activeRoot in tabStacks) {
            "activeRoot $activeRoot must be present in tabStacks ${tabStacks.keys}"
        }
        tabStacks.forEach { (root, stack) ->
            require(stack.isNotEmpty()) { "Tab stack for $root must contain at least its root entry" }
        }
    }

    /**
     * Projects every entry across every tab as a [ChildNavState]. Only the active tab's top is
     * [ChildNavState.Status.RESUMED]; everything else is [ChildNavState.Status.CREATED].
     *
     * Decompose's canonical neighbours are typically [ChildNavState.Status.STARTED] (ChildPages
     * `INACTIVE`) or [ChildNavState.Status.CREATED] (ChildStack back stack). [CREATED] is chosen
     * here so coroutines wired through `lifecycle.coroutineScope()` do not run for inactive tabs,
     * conserving CPU and battery on bottom-nav re-entry.
     *
     * Tab presenters must use `stateIn(scope, SharingStarted.WhileSubscribed(...))` for any flow
     * that needs to refresh on tab re-entry; the flow restarts on UI subscription, independent of
     * lifecycle status.
     */
    override val children: List<ChildNavState<TabbedRoute>> by lazy {
        tabStacks.flatMap { (root, stack) ->
            stack.mapIndexed { index, entry ->
                SimpleChildNavState(
                    configuration = TabbedRoute(tabRoot = root, route = entry),
                    status = when {
                        root == activeRoot && index == stack.lastIndex -> ChildNavState.Status.RESUMED
                        else -> ChildNavState.Status.CREATED
                    },
                )
            }
        }
    }
}

/**
 * Builds the initial [MultiStackNavState] with one root entry for each registered tab and
 * [initialRoot] active. Used once when the activity has no saved state to restore.
 */
internal fun multiStackInitialState(navRoots: Set<NavRoot>, initialRoot: NavRoot): MultiStackNavState =
    MultiStackNavState(
        activeRoot = initialRoot,
        tabStacks = navRoots.associateWith { listOf(it as BaseRoute) },
    )

/**
 * Applies [event] to [state] and returns the next [MultiStackNavState].
 *
 * Push, pop, and dedupe events target the active tab's stack only. [MultiStackNavEvent.SwitchTab]
 * and [MultiStackNavEvent.ReplaceAll] change which tab is active and may reset stacks. Unregistered
 * roots throw at this level so the navigator surfaces misconfigurations close to the call site.
 */
internal fun multiStackNavTransformer(
    state: MultiStackNavState,
    event: MultiStackNavEvent,
): MultiStackNavState = when (event) {
    is MultiStackNavEvent.Push -> state.withActiveStack { it + (event.route as BaseRoute) }

    is MultiStackNavEvent.BringToFront -> state.withActiveStack { stack ->
        stack.filterNot { it::class == event.route::class } + (event.route as BaseRoute)
    }

    is MultiStackNavEvent.PushToFront -> state.withActiveStack { stack ->
        stack.filterNot { it == event.route } + (event.route as BaseRoute)
    }

    is MultiStackNavEvent.Pop -> state.withActiveStack { stack ->
        when {
            event.toIndex < 0 -> stack.dropLast(1).ifEmpty { stack }
            event.toIndex < stack.size -> stack.take(event.toIndex + 1)
            else -> stack
        }
    }

    is MultiStackNavEvent.PopUntilType -> state.withActiveStack { stack ->
        val targetIndex = stack.indexOfLast { event.routeClass.isInstance(it) }
        if (targetIndex < 0) {
            stack
        } else {
            val keep = if (event.inclusive) targetIndex else targetIndex + 1
            if (keep <= 0) stack else stack.take(keep)
        }
    }

    is MultiStackNavEvent.SwitchTab -> {
        require(event.root in state.tabStacks) {
            "NavRoot ${event.root} is not registered. Add it to Set<NavRoot>."
        }
        val newStacks = if (event.resetStack) {
            state.tabStacks + (event.root to listOf(event.root as BaseRoute))
        } else {
            state.tabStacks
        }
        state.copy(activeRoot = event.root, tabStacks = newStacks)
    }

    is MultiStackNavEvent.ReplaceAll -> {
        require(event.root in state.tabStacks) {
            "NavRoot ${event.root} is not registered. Add it to Set<NavRoot>."
        }
        val resetStacks = state.tabStacks.mapValues { (root, _) -> listOf(root as BaseRoute) }
        state.copy(activeRoot = event.root, tabStacks = resetStacks)
    }
}

private inline fun MultiStackNavState.withActiveStack(
    transform: (List<BaseRoute>) -> List<BaseRoute>,
): MultiStackNavState {
    val current = tabStacks[activeRoot] ?: error("Active root $activeRoot missing from tabStacks")
    val next = transform(current)
    if (next == current) return this
    return copy(tabStacks = tabStacks + (activeRoot to next))
}
