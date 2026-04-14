package com.thomaskioko.tvmaniac.navigation

/**
 * A generic typed wrapper that holds a presenter as a [SheetChild] in a modal sheet slot.
 *
 * This is the sheet equivalent of [ScreenDestination]. The concrete presenter type [T] is
 * only known at the creation site and the UI layer, keeping the navigation API free of
 * presenter-specific types.
 *
 * @param T The presenter type held by this sheet destination
 * @param presenter The presenter instance for the sheet
 */
public class SheetDestination<out T : Any>(public val presenter: T) : SheetChild
