package com.thomaskioko.tvmaniac.compose.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp

private val LOCKED_BLUR_RADIUS = 16.dp
private val LOCKED_LINE_SPACING = 12.dp
private const val LOCKED_WASH_ALPHA = 0.67f
private const val LOCKED_FALLBACK_WASH_ALPHA = 0.9f
private const val LOCKED_CARD_ALPHA = 0.65f

@Composable
private fun PremiumOverlay(
    locked: Boolean,
    modifier: Modifier = Modifier,
    overlayContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val blurCapable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val shouldBlur = locked && blurCapable && !LocalInspectionMode.current
    val washAlpha = if (blurCapable) LOCKED_WASH_ALPHA else LOCKED_FALLBACK_WASH_ALPHA
    val washColor = MaterialTheme.colorScheme.background.copy(alpha = washAlpha)
    val lineColor = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .then(if (shouldBlur) Modifier.blur(LOCKED_BLUR_RADIUS, BlurredEdgeTreatment.Rectangle) else Modifier)
                .then(if (locked) Modifier.clearAndSetSemantics { } else Modifier),
        ) {
            content()
        }

        if (locked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(washColor)
                    .drawBehind {
                        val spacing = LOCKED_LINE_SPACING.toPx()
                        val stroke = 1.dp.toPx()
                        var startX = 0f
                        while (startX < size.width) {
                            drawLine(
                                color = lineColor,
                                start = Offset(startX, 0f),
                                end = Offset(size.width, size.width - startX),
                                strokeWidth = stroke,
                            )
                            startX += spacing
                        }
                        var startY = spacing
                        while (startY < size.height) {
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, startY),
                                end = Offset(size.height - startY, size.height),
                                strokeWidth = stroke,
                            )
                            startY += spacing
                        }
                    }
                    .pointerInput(Unit) { detectTapGestures { } },
                contentAlignment = Alignment.Center,
            ) {
                overlayContent()
            }
        }
    }
}

@Composable
public fun PremiumOverlay(
    locked: Boolean,
    badgeText: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    PremiumOverlay(
        locked = locked,
        modifier = modifier,
        overlayContent = {
            Surface(
                modifier = Modifier.padding(horizontal = 32.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = LOCKED_CARD_ALPHA),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PremiumBadge(text = badgeText)

                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }

                    message?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                        )
                    }

                    if (actionText != null && onActionClick != null) {
                        FilledTextButton(
                            onClick = onActionClick,
                            shape = MaterialTheme.shapes.medium,
                            buttonColors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                            ),
                        ) {
                            Text(
                                text = actionText,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        },
        content = content,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PremiumOverlayPreview() {
    PremiumOverlay(
        locked = true,
        badgeText = "Premium",
        title = "Calendar is a Premium feature",
        message = "Upgrade to Premium to see your upcoming episodes.",
        actionText = "Upgrade to Premium",
        onActionClick = {},
        modifier = Modifier.size(width = 320.dp, height = 280.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PremiumOverlayUnlockedPreview() {
    PremiumOverlay(
        locked = false,
        badgeText = "Premium",
        modifier = Modifier.size(width = 320.dp, height = 240.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}
