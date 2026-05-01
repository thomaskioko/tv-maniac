package com.thomaskioko.tvmaniac.compose.util

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Controls the auto-advance LaunchedEffect on the Discover featured pager (`PosterCardsPager`).
 *
 * Defaults to `true` in production: every 4.5 seconds the pager animates to the next page so the
 * featured carousel cycles on its own. Tests can override this to `false` via
 * `CompositionLocalProvider(LocalAutoAdvanceEnabled provides false)` so that the visible page
 * stays where the test left it. This makes pager assertions deterministic regardless of how much
 * wall-time has elapsed since activity launch.
 */
public val LocalAutoAdvanceEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf {
    true
}
