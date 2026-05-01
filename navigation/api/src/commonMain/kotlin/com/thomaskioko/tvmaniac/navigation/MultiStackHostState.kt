package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack

/**
 * Snapshot of the multi-stack navigation host produced by [Navigator.buildHostNavigation].
 *
 * Holds the currently active [NavRoot] and a per-tab [ChildStack] keyed by [NavRoot]. Render-side
 * presenters project per-tab stacks from [tabStacks] and observe [activeRoot] to decide which tab
 * pane is visible.
 *
 * @param T component type produced by the host's `childFactory`.
 */
public data class MultiStackHostState<out T : Any>(
    public val activeRoot: NavRoot,
    public val tabStacks: Map<NavRoot, ChildStack<BaseRoute, T>>,
)
