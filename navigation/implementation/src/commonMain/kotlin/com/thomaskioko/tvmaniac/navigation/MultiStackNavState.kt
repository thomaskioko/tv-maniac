package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState

internal data class TabbedRoute(
    val tabRoot: NavRoot,
    val route: BaseRoute,
)

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

internal fun multiStackInitialState(navRoots: Set<NavRoot>, initialRoot: NavRoot): MultiStackNavState =
    MultiStackNavState(
        activeRoot = initialRoot,
        tabStacks = navRoots.associateWith { listOf(it as BaseRoute) },
    )

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
