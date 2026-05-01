package com.thomaskioko.tvmaniac.navigation

/**
 * Marker interface implemented by each feature's `@Serializable` route class in its `nav` module.
 *
 * Each route class is registered as a Metro multibinding via [NavRouteBinding] so the Decompose
 * stack polymorphically serializes and deserializes routes without a central sealed hierarchy.
 * Adding a new route only touches the feature's own `nav` module plus its `NavDestination`
 * contribution; `navigation/api` and the root presenter stay untouched.
 *
 * Extends [BaseRoute] alongside [NavRoot]; routes that act as overlays additionally implement
 * [OverlayRoute] so [Navigator.navigateTo] dispatches them to the overlay slot rather than the
 * active back stack.
 */
public interface NavRoute : BaseRoute
