package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.AquaColorScheme
import com.thomaskioko.tvmaniac.compose.theme.AutumnColorScheme
import com.thomaskioko.tvmaniac.compose.theme.DarkColorScheme
import com.thomaskioko.tvmaniac.compose.theme.LightColorScheme
import com.thomaskioko.tvmaniac.compose.theme.TerminalColorScheme
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel

@Composable
internal fun ThemePreviewSwatch(
    theme: ThemeModel,
    displayName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = theme.getPreviewColors()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (theme == ThemeModel.SYSTEM) {
                SystemThemeSwatch(
                    isSelected = isSelected,
                    modifier = Modifier.size(56.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colors.background)
                        .then(
                            if (isSelected) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colors.accent),
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (theme == ThemeModel.SYSTEM) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                colors.accent
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = if (theme == ThemeModel.SYSTEM) {
                            Color.White
                        } else {
                            colors.onAccent
                        },
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@Composable
private fun SystemThemeSwatch(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val lightColors = LightColorScheme
    val darkColors = DarkColorScheme
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    val borderWidth = if (isSelected) 3.dp else 1.dp

    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .border(borderWidth, borderColor, CircleShape),
    ) {
        val width = size.width
        val height = size.height

        val lightPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(0f, height)
            close()
        }

        val darkPath = Path().apply {
            moveTo(width, 0f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(lightPath, lightColors.background)
        drawPath(darkPath, darkColors.background)

        drawCircle(
            color = lightColors.secondary,
            radius = width * 0.15f,
            center = Offset(width * 0.35f, height * 0.35f),
        )

        drawCircle(
            color = darkColors.secondary,
            radius = width * 0.15f,
            center = Offset(width * 0.65f, height * 0.65f),
        )
    }
}

internal data class ThemePreviewColors(
    val background: Color,
    val accent: Color,
    val onAccent: Color,
)

@Composable
private fun ThemeModel.getPreviewColors(): ThemePreviewColors {
    return when (this) {
        ThemeModel.SYSTEM -> ThemePreviewColors(
            background = MaterialTheme.colorScheme.background,
            accent = MaterialTheme.colorScheme.secondary,
            onAccent = MaterialTheme.colorScheme.onSecondary,
        )
        ThemeModel.LIGHT -> ThemePreviewColors(
            background = LightColorScheme.background,
            accent = LightColorScheme.secondary,
            onAccent = LightColorScheme.onSecondary,
        )
        ThemeModel.DARK -> ThemePreviewColors(
            background = DarkColorScheme.background,
            accent = DarkColorScheme.secondary,
            onAccent = DarkColorScheme.onSecondary,
        )
        ThemeModel.TERMINAL -> ThemePreviewColors(
            background = TerminalColorScheme.background,
            accent = TerminalColorScheme.secondary,
            onAccent = TerminalColorScheme.onSecondary,
        )
        ThemeModel.AUTUMN -> ThemePreviewColors(
            background = AutumnColorScheme.background,
            accent = AutumnColorScheme.secondary,
            onAccent = AutumnColorScheme.onSecondary,
        )
        ThemeModel.AQUA -> ThemePreviewColors(
            background = AquaColorScheme.background,
            accent = AquaColorScheme.secondary,
            onAccent = AquaColorScheme.onSecondary,
        )
    }
}

private class ThemeSwatchPreviewProvider : PreviewParameterProvider<Pair<ThemeModel, Boolean>> {
    override val values: Sequence<Pair<ThemeModel, Boolean>>
        get() = sequenceOf(
            ThemeModel.SYSTEM to false,
            ThemeModel.SYSTEM to true,
            ThemeModel.LIGHT to false,
            ThemeModel.LIGHT to true,
            ThemeModel.DARK to false,
            ThemeModel.DARK to true,
            ThemeModel.TERMINAL to false,
            ThemeModel.TERMINAL to true,
            ThemeModel.AUTUMN to false,
            ThemeModel.AUTUMN to true,
            ThemeModel.AQUA to false,
            ThemeModel.AQUA to true,
        )
}

@ThemePreviews
@Composable
private fun ThemePreviewSwatchPreview(
    @PreviewParameter(ThemeSwatchPreviewProvider::class) themeState: Pair<ThemeModel, Boolean>,
) {
    val (theme, isSelected) = themeState
    com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme {
        ThemePreviewSwatch(
            theme = theme,
            displayName = theme.name.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() },
            isSelected = isSelected,
            onClick = {},
        )
    }
}
