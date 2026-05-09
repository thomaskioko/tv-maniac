package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import kotlin.reflect.KClass

/**
 * Sealed factory family for resolving a [BaseRoute] to its [RootChild] wrapper.
 *
 * Each feature contributes one [NavDestination] subtype via `@ContributesIntoSet(ActivityScope::class)`
 * (typically through codegen). The hosts (`DefaultRootPresenter` for the overlay slot,
 * `HomePresenter` for the multi-stack body) collect every contribution as `Set<NavDestination<*>>`,
 * filter by subtype, match by route class, and delegate to [Screen.createChild] / [Overlay.createChild]
 * / [TabRoot.createChild]. A central when-block stays out of the root module.
 *
 * The three subtypes mirror the runtime dispatch in [Navigator.navigateTo]:
 * - [Screen] — pushed onto the active tab's back stack.
 * - [Overlay] — activated as a modal in the overlay slot.
 * - [TabRoot] — the bottom-of-stack entry for one of the registered [NavRoot] tabs.
 *
 * @param R route type this destination matches.
 * @property routeClass concrete `KClass` used for runtime matching against an incoming [BaseRoute].
 */
public sealed class NavDestination<out R : BaseRoute>(
    public val routeClass: KClass<out R>,
) {
    /** Returns `true` if this destination handles [route]. */
    public fun matches(route: BaseRoute): Boolean = routeClass.isInstance(route)

    /**
     * Stack screen pushed via [Navigator.navigateTo] on the active tab's back stack.
     */
    public class Screen<R : NavRoute>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }

    /**
     * Modal overlay activated via [Navigator.navigateTo] when the route also implements [OverlayRoute].
     * The host (typically `DefaultRootPresenter`) renders this in the overlay slot.
     */
    public class Overlay<R : NavRoute>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }

    /**
     * Tab root anchoring its own back stack. Resolved by `HomePresenter` when projecting per-tab
     * [com.arkivanov.decompose.router.stack.ChildStack] children.
     */
    public class TabRoot<R : NavRoot>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }
}
