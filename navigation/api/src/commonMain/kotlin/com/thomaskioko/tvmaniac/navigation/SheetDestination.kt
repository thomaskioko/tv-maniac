package com.thomaskioko.tvmaniac.navigation

/**
 * Wraps a feature presenter as a [SheetChild] in the modal sheet slot.
 *
 * Sheet counterpart to [ScreenDestination]. The concrete presenter type [T] is known only at the
 * creation site (the feature's [NavDestination.Overlay]) and at the consumption site (the platform
 * UI), so `navigation/api` stays free of presenter-specific types.
 *
 * @param T presenter type held by this sheet destination.
 * @property presenter sheet presenter rendered by the platform UI.
 */
public class SheetDestination<out T : Any>(public val presenter: T) : SheetChild
