package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext

/**
 * Factory for creating [SheetChild] instances from a [SheetConfig].
 *
 * Each feature that owns a sheet contributes one implementation via
 * `@Provides @IntoSet` in its presenter module. The root presenter collects every contribution as
 * a `Set<SheetChildFactory>` and, when the sheet slot activates, walks the set, picks the first
 * factory whose [matches] returns `true`, and delegates to [createChild]. This mirrors the
 * [NavDestination] pattern used for the main stack.
 */
public interface SheetChildFactory {
    /** Returns `true` if this factory can handle [config]. */
    public fun matches(config: SheetConfig): Boolean

    /**
     * Creates the [SheetChild] for [config] under [componentContext].
     *
     * Only called after [matches] returned `true` for the same [config].
     */
    public fun createChild(config: SheetConfig, componentContext: ComponentContext): SheetChild
}
