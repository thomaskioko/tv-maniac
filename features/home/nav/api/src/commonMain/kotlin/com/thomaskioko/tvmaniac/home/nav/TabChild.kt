package com.thomaskioko.tvmaniac.home.nav

/**
 * A generic typed wrapper that holds a tab presenter as a child of the home screen.
 *
 * The concrete presenter type [T] is only known at the creation site (each tab's
 * `presenter/di/` wiring) and the consumption site (the home UI).
 *
 * @param T The presenter type held by this tab child
 * @param presenter The presenter instance for the tab
 */
public class TabChild<out T : Any>(public val presenter: T)
