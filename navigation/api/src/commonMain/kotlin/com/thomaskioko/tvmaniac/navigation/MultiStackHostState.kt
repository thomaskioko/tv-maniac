package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack

/**
 * Snapshot returned from [Navigator.buildHostNavigation] containing the active tab and a
 * [ChildStack] for each registered [NavRoot].
 *
 * Render-side presenters read [tabStacks] for the back stack of each tab and observe [activeRoot]
 * to decide which tab pane is visible.
 *
 * @param T component type produced by the host's `childFactory`.
 * @property activeRoot tab whose top entry is currently visible.
 * @property tabStacks back stack for each registered tab, keyed by [NavRoot].
 */
public data class MultiStackHostState<out T : Any>(
    public val activeRoot: NavRoot,
    public val tabStacks: Map<NavRoot, ChildStack<BaseRoute, T>>,
)
