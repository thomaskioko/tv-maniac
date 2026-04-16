package com.thomaskioko.tvmaniac.navigation

/**
 * Generic typed [SheetChild] that wraps a feature presenter in the modal sheet slot.
 *
 * Sheet counterpart to [ScreenDestination]. The concrete presenter type [T] is known only at
 * the creation site (the feature's [SheetChildFactory]) and at the consumption site (the
 * platform UI), keeping the navigation API free of presenter-specific types.
 *
 * @param T the presenter type held by this sheet destination
 * @param presenter the presenter instance for the sheet
 */
public class SheetDestination<out T : Any>(public val presenter: T) : SheetChild
