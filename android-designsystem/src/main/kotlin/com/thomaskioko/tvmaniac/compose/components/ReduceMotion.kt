package com.thomaskioko.tvmaniac.compose.components

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Reports whether the user disabled animations system-wide. Reads
 * [Settings.Global.ANIMATOR_DURATION_SCALE]; a scale of `0` means animations are off.
 *
 * @return `true` when motion should be suppressed and final states shown immediately.
 */
@Composable
public fun rememberReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }
}
