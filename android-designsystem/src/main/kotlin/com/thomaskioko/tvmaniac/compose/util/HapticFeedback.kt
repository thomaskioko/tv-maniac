package com.thomaskioko.tvmaniac.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Whether haptic feedback fires on supported interactions. Provided at the application root from the
 * persisted preference; defaults to `true`. Gate every haptic call site through
 * [rememberHapticFeedback] so flipping this off silences them all.
 */
public val LocalHapticFeedbackEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf {
    true
}

/**
 * Returns a callback that performs a haptic pulse when [LocalHapticFeedbackEnabled] is on and is a
 * no-op otherwise. Call at interaction sites like watched toggles and long presses.
 */
@Composable
public fun rememberHapticFeedback(): () -> Unit {
    val enabled = LocalHapticFeedbackEnabled.current
    val haptics = LocalHapticFeedback.current
    return remember(enabled, haptics) {
        { if (enabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress) }
    }
}
