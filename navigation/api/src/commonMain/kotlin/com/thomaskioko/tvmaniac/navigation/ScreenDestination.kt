package com.thomaskioko.tvmaniac.navigation

/**
 * Generic typed [RootChild] that wraps a feature presenter.
 *
 * Using a single generic wrapper avoids declaring a per-feature child class (for example
 * `SettingsDestination`, `DebugDestination`) and keeps `navigation/api` free of presenter-
 * specific types. The concrete presenter type [T] is known only at the creation site
 * (the feature's [NavDestination]) and at the consumption site (the platform UI), which
 * pattern-matches on `presenter` to render the correct screen.
 *
 * @param T the presenter type held by this destination
 * @param presenter the presenter instance for the screen
 */
public class ScreenDestination<out T : Any>(public val presenter: T) : RootChild
