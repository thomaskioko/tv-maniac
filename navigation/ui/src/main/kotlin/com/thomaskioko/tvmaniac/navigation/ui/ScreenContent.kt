package com.thomaskioko.tvmaniac.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thomaskioko.tvmaniac.navigation.RootChild

/**
 * Platform-side renderer for a [RootChild] in the root navigation stack.
 *
 * Mirrors the shared-code [com.thomaskioko.tvmaniac.navigation.NavDestination] multibinding, but for
 * the Compose UI layer. Each feature `ui` module contributes one [ScreenContent] via
 * `@ContributesTo(ActivityScope::class)` with `@Provides @IntoSet`. The root Compose stack iterates
 * the injected `Set<ScreenContent>`, picks the first whose [matches] returns `true`, and invokes
 * [content] to render the screen. This keeps `features/root/ui` free of per-feature imports.
 */
public class ScreenContent(
    public val matches: (RootChild) -> Boolean,
    public val content: @Composable (RootChild, Modifier) -> Unit,
)
