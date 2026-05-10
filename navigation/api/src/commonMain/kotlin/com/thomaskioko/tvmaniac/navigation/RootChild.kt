package com.thomaskioko.tvmaniac.navigation

/**
 * Marks a value stored in each entry of the root Decompose stack.
 *
 * Concrete implementations (most commonly [ScreenDestination]) wrap a feature presenter so the
 * root UI can pattern-match on the child type and render the matching composable or SwiftUI view.
 * Direct implementations are rare; prefer [ScreenDestination].
 */
public interface RootChild
