package com.thomaskioko.tvmaniac.navigation

/**
 * Common parent for every navigation target in the multi-stack model.
 *
 * Two direct subtypes split the navigation surface by role: [NavRoute] for stack entries that
 * sit inside a back stack, and [NavRoot] for top-level destinations that anchor each back stack
 * (typically the four bottom-tab roots). [Navigator] uses the type distinction to dispatch
 * screen pushes ([NavRoute]) and tab switches ([NavRoot]) to different internal sources.
 *
 * Sealed because the framework owns both subtypes; feature `:nav` modules implement [NavRoute],
 * [NavRoot], or [OverlayRoute] directly, never [BaseRoute].
 */
public sealed interface BaseRoute
