package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import kotlin.reflect.KClass

/**
 * Maps a [BaseRoute] to its [RootChild] wrapper through one of three subtypes.
 *
 * Each feature contributes one [NavDestination] subtype via `@ContributesIntoSet(ActivityScope::class)`
 * (typically through codegen). The hosts (`DefaultRootPresenter` for the overlay slot,
 * `HomePresenter` for the multi-stack body) collect every contribution as `Set<NavDestination<*>>`,
 * filter by subtype, match by route class, and delegate to [Screen.createChild],
 * [Overlay.createChild], or [TabRoot.createChild]. A central when-block stays out of the root module.
 *
 * The three subtypes mirror the runtime dispatch in [Navigator.navigateTo]:
 * - [Screen]: pushed onto the active tab's back stack.
 * - [Overlay]: activated as a modal in the overlay slot.
 * - [TabRoot]: the bottom-of-stack entry for one of the registered [NavRoot] tabs.
 *
 * @param R route type this destination matches.
 * @property routeClass matched against incoming [BaseRoute] instances at dispatch time.
 */
public sealed class NavDestination<out R : BaseRoute>(
    public val routeClass: KClass<out R>,
) {
    /** Returns `true` if this destination handles [route]. */
    public fun matches(route: BaseRoute): Boolean = routeClass.isInstance(route)

    /**
     * Stack screen pushed via [Navigator.navigateTo] onto the active tab's back stack.
     *
     * @param R route subtype this destination matches.
     */
    public class Screen<R : NavRoute>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        /**
         * Creates the [RootChild] for [route] using the configured factory. Callers verify the
         * route's class matches [routeClass] before calling.
         *
         * @param route stack entry matched against [routeClass].
         * @param componentContext Decompose context for the new child.
         */
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }

    /**
     * Modal overlay activated via [Navigator.navigateTo] when the route also implements
     * [OverlayRoute]. The host (typically `DefaultRootPresenter`) renders this in the overlay slot.
     *
     * @param R route subtype this destination matches.
     */
    public class Overlay<R : NavRoute>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        /**
         * Creates the [RootChild] for [route] using the configured factory. The result is rewrapped
         * by `DefaultRootPresenter.createOverlay` for the overlay slot.
         *
         * @param route overlay route matched against [routeClass].
         * @param componentContext Decompose context for the new child.
         */
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }

    /**
     * Tab root anchoring its own back stack. Resolved by `HomePresenter` when projecting the
     * [com.arkivanov.decompose.router.stack.ChildStack] for each registered tab.
     *
     * @param R [NavRoot] subtype this destination matches.
     */
    public class TabRoot<R : NavRoot>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        /**
         * Creates the [RootChild] for [route] using the configured factory. Invoked once for each
         * tab when the host stack is built.
         *
         * @param route tab anchor matched against [routeClass].
         * @param componentContext Decompose context for the new child.
         */
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }
}
