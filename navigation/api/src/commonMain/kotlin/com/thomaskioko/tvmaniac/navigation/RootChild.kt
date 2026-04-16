package com.thomaskioko.tvmaniac.navigation

/**
 * Marker interface for the value stored in each entry of the root Decompose stack.
 *
 * Concrete implementations (most commonly [ScreenDestination]) wrap a feature presenter so that
 * the root UI can pattern-match on the child type and render the matching composable or SwiftUI
 * view. Implementing this interface in a feature module is rare; prefer [ScreenDestination].
 */
public interface RootChild
