package com.thomaskioko.tvmaniac.navigation

/**
 * Wraps a feature presenter as a [RootChild] in the active back stack.
 *
 * One generic wrapper avoids a child class for each feature (for example `SettingsDestination`,
 * `DebugDestination`) and keeps `navigation/api` free of presenter-specific types. The concrete
 * presenter type [T] is known only at the creation site (the feature's [NavDestination]) and at
 * the consumption site (the platform UI), which pattern-matches on [presenter] to render the
 * correct screen.
 *
 * @param T presenter type held by this destination.
 * @property presenter screen presenter rendered by the platform UI.
 */
public class ScreenDestination<out T : Any>(public val presenter: T) : RootChild
