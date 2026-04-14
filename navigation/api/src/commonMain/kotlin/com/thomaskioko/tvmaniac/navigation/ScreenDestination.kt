package com.thomaskioko.tvmaniac.navigation

/**
 * A generic typed wrapper that holds a presenter as a [RootChild] in the navigation stack.
 *
 * This replaces per-feature destination classes (e.g., `SettingsDestination`) with a single
 * generic wrapper, keeping `navigation/api` free of presenter-specific types. The concrete
 * presenter type [T] is only known at the creation site (nav/implementation) and the
 * consumption site (UI layer), where it is pattern-matched to render the appropriate screen.
 *
 * @param T The presenter type held by this destination
 * @param presenter The presenter instance for the screen
 */
public class ScreenDestination<out T : Any>(public val presenter: T) : RootChild
