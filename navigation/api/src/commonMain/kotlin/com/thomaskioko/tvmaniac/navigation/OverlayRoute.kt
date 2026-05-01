package com.thomaskioko.tvmaniac.navigation

/**
 * Marker for [NavRoute] subtypes that activate as overlays (modal sheets) rather than full-screen
 * stack entries.
 *
 * [Navigator.navigateTo] inspects the route's type at runtime: a route that implements
 * [OverlayRoute] dispatches to the overlay slot; everything else dispatches to the active root's
 * back stack. The marker keeps overlay routing inside [Navigator] without splitting the public
 * method into two.
 */
public interface OverlayRoute : NavRoute
