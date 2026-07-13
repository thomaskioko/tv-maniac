package com.thomaskioko.tvmaniac.compose.util

import android.os.Build
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

public val LocalBlurUnwatchedEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf {
    false
}

private val BLUR_EFFECT_RADIUS = 24.dp
private const val BLUR_EFFECT_SCRIM_ALPHA = 0.6f

public fun Modifier.blurEffect(enabled: Boolean): Modifier = when {
    !enabled -> this
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
        blur(BLUR_EFFECT_RADIUS, BlurredEdgeTreatment.Rectangle)
    else -> drawWithContent {
        drawContent()
        drawRect(Color.Black.copy(alpha = BLUR_EFFECT_SCRIM_ALPHA))
    }
}
