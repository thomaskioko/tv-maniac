package com.thomaskioko.tvmaniac.home.nav

import com.thomaskioko.tvmaniac.navigation.RootChild

/**
 * Generic typed wrapper holding a tab presenter as a child of the home screen.
 *
 * Concrete presenter type [T] is known at the creation site (each tab's `presenter/di/`
 * wiring) and the consumption site (the home UI). Implements [RootChild] so per-tab
 * back stacks can mix [TabChild] (tab roots) and other [RootChild] subtypes (pushed
 * screens) in one [com.arkivanov.decompose.router.stack.ChildStack].
 *
 * @param T Presenter type held by this tab child.
 * @param presenter Presenter instance for the tab.
 */
public class TabChild<out T : Any>(public val presenter: T) : RootChild
