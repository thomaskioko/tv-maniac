package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig

/**
 * Factory for creating [RootChild] instances from a [RootDestinationConfig].
 *
 * Each feature contributes an implementation via DI multibinding. The root presenter
 * collects all contributions as a `Set<NavDestination>` and dispatches to the matching
 * one when creating a new screen in the navigation stack.
 */
public interface NavDestination {
    /**
     * Returns `true` if this destination can handle the given [config].
     *
     * @param config The navigation configuration to check
     */
    public fun matches(config: RootDestinationConfig): Boolean

    /**
     * Creates a [RootChild] for the given [config] and [componentContext].
     * Only called when [matches] returns `true`.
     *
     * @param config The navigation configuration for the screen
     * @param componentContext The Decompose component context for the new child
     */
    public fun createChild(config: RootDestinationConfig, componentContext: ComponentContext): RootChild
}
