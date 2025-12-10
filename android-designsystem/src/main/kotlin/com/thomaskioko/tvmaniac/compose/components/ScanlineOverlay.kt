package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.datastore.api.AppTheme

data class ScanlineConfiguration(
    val enabled: Boolean,
    val color: Color,
    val lineHeight: Dp = 2.dp,
    val opacity: Float = 0.15f,
) {
    companion object {
        val Disabled = ScanlineConfiguration(enabled = false, color = Color.Transparent)

        fun terminal() = ScanlineConfiguration(
            enabled = true,
            color = Color(0xFF20C020),
            opacity = 0.12f,
        )

        fun amber() = ScanlineConfiguration(
            enabled = true,
            color = Color(0xFFFF8C00),
            opacity = 0.12f,
        )

        fun snow() = ScanlineConfiguration(
            enabled = true,
            color = Color(0xFFFFFFFF),
            opacity = 0.08f,
        )

        fun crimson() = ScanlineConfiguration(
            enabled = true,
            color = Color(0xFFFF4D6A),
            opacity = 0.12f,
        )
    }
}

fun AppTheme.toScanlineConfiguration(): ScanlineConfiguration = when (this) {
    AppTheme.TERMINAL_THEME -> ScanlineConfiguration.terminal()
    AppTheme.AMBER_THEME -> ScanlineConfiguration.amber()
    AppTheme.SNOW_THEME -> ScanlineConfiguration.snow()
    AppTheme.CRIMSON_THEME -> ScanlineConfiguration.crimson()
    else -> ScanlineConfiguration.Disabled
}

@Composable
fun ScanlineOverlay(
    configuration: ScanlineConfiguration,
    modifier: Modifier = Modifier,
) {
    if (!configuration.enabled) return

    val lineColor = configuration.color.copy(alpha = configuration.opacity)

    Canvas(modifier = modifier.fillMaxSize()) {
        val lineHeightPx = configuration.lineHeight.toPx()
        val lineSpacing = lineHeightPx * 2
        var y = 0f

        while (y < size.height) {
            drawRect(
                color = lineColor,
                topLeft = Offset(0f, y),
                size = Size(size.width, lineHeightPx),
            )
            y += lineSpacing
        }
    }
}
