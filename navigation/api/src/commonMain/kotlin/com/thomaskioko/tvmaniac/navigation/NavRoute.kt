package com.thomaskioko.tvmaniac.navigation

/**
 * Marker interface implemented by each feature's `@Serializable` route class in its `nav`
 * module.
 *
 * Each route class is registered as a Metro multibinding via [NavRouteBinding] so that the
 * Decompose stack can polymorphically serialize and deserialize routes without a central sealed
 * hierarchy. Adding a new route therefore only touches the feature's own `nav` module plus
 * its `NavDestination` contribution; `navigation/api` and the root presenter stay untouched.
 */
public interface NavRoute
