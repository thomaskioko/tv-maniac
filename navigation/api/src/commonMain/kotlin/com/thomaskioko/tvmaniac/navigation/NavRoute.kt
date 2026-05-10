package com.thomaskioko.tvmaniac.navigation

/**
 * Marks a feature's `@Serializable` route class as a back stack entry.
 *
 * Each route class registers as a Metro multibinding through [NavRouteBinding] so the Decompose
 * stack polymorphically serializes and deserializes routes without a central sealed hierarchy.
 * Adding a new route touches the feature's own `nav` module and its `NavDestination` contribution
 * only; `navigation/api` and the root presenter stay untouched.
 *
 * Extends [BaseRoute] alongside [NavRoot]. Routes that act as overlays additionally implement
 * [OverlayRoute] so [Navigator.navigateTo] dispatches them to the overlay slot rather than the
 * active back stack.
 */
public interface NavRoute : BaseRoute
