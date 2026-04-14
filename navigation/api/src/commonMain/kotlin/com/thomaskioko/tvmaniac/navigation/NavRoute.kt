package com.thomaskioko.tvmaniac.navigation

/**
 * Marker interface implemented by each feature's `@Serializable` route class in its `nav/api`
 * module. Each route class is registered as a Metro multibinding via [NavRouteBinding] so that
 * the Decompose stack can polymorphically serialize and deserialize routes without a central
 * sealed hierarchy.
 */
public interface NavRoute
