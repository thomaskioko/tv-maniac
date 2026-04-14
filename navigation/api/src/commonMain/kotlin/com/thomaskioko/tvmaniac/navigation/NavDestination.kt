package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext

/**
 * Factory for creating [RootChild] instances from a [NavRoute].
 *
 * Each feature contributes an implementation via DI multibinding. The root presenter
 * collects all contributions as a `Set<NavDestination>` and dispatches to the matching
 * one when creating a new screen in the navigation stack.
 */
public interface NavDestination {
    /**
     * Returns `true` if this destination can handle the given [route].
     *
     * @param route The navigation route to check
     */
    public fun matches(route: NavRoute): Boolean

    /**
     * Creates a [RootChild] for the given [route] and [componentContext].
     * Only called when [matches] returns `true`.
     *
     * @param route The navigation route for the screen
     * @param componentContext The Decompose component context for the new child
     */
    public fun createChild(route: NavRoute, componentContext: ComponentContext): RootChild
}
