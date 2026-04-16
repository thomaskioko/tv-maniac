package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext

/**
 * Factory for creating [RootChild] instances from a [NavRoute].
 *
 * Each feature contributes one implementation via `@ContributesIntoSet(ActivityScope::class)`.
 * The root presenter collects every contribution as a `Set<NavDestination>` and, when a new
 * stack entry is created, walks the set, picks the first destination whose [matches] returns
 * `true`, and delegates to [createChild]. This replaces a central when-block over a sealed
 * hierarchy so adding a new screen only touches its own feature module.
 */
public interface NavDestination {
    /** Returns `true` if this destination can handle [route]. */
    public fun matches(route: NavRoute): Boolean

    /**
     * Creates the [RootChild] for [route] under [componentContext].
     *
     * Only called after [matches] returned `true` for the same [route].
     */
    public fun createChild(route: NavRoute, componentContext: ComponentContext): RootChild
}
