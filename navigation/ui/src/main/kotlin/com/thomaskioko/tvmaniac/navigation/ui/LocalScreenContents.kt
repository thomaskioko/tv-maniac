package com.thomaskioko.tvmaniac.navigation.ui

import androidx.compose.runtime.compositionLocalOf

/**
 * Composition-scoped access to the [ScreenContent] multibinding.
 *
 * Provided once at the root composable (`RootScreen`) and read by descendants that render their
 * own child stacks. Avoids threading [ScreenContent] through every nested composable signature
 * when intermediate UI containers (such as the home tab host) need to dispatch pushed screens.
 */
public val LocalScreenContents: androidx.compose.runtime.ProvidableCompositionLocal<Set<ScreenContent>> =
    compositionLocalOf { emptySet() }
