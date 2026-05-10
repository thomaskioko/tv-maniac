package com.thomaskioko.tvmaniac.navigation

/**
 * Marks a [NavRoute] subtype that activates as an overlay (a modal sheet) rather than a
 * full-screen stack entry.
 *
 * [Navigator.navigateTo] inspects the route's type at runtime. A route that implements
 * [OverlayRoute] dispatches to the overlay slot; every other route dispatches to the active
 * root's back stack. The marker keeps overlay routing inside [Navigator] without splitting the
 * public method into two.
 */
public interface OverlayRoute : NavRoute
