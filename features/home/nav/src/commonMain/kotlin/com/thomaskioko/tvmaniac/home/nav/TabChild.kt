package com.thomaskioko.tvmaniac.home.nav

import com.thomaskioko.tvmaniac.navigation.RootChild

/**
 * Wraps a tab presenter as a [RootChild] in the home screen's stack.
 *
 * The concrete presenter type [T] is known at the creation site (each tab's `presenter/di/`
 * binding) and at the consumption site (the home UI). Implements [RootChild] so each tab's back
 * stack can mix [TabChild] (tab roots) with other [RootChild] subtypes (pushed screens) inside
 * one [com.arkivanov.decompose.router.stack.ChildStack].
 *
 * @param T presenter type held by this tab child.
 * @property presenter tab presenter rendered by the home UI.
 */
public class TabChild<out T : Any>(public val presenter: T) : RootChild
