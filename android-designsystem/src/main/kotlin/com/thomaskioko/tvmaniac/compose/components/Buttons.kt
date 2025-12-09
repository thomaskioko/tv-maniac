package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.AppTheme

@Composable
fun FilledTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(),
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = buttonColors,
        content = content,
        shape = shape,
    )
}

@Composable
fun FilledVerticalIconButton(
    text: String,
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
) {
    TextButtonContent(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerColor = containerColor,
        shape = shape,
        content = {
            Column(
                modifier = Modifier
                    .sizeIn(minWidth = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = when {
                        enabled -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                )

                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = text,
                    style = style,
                    color = when {
                        enabled -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                )
            }
        },
    )
}

@Composable
fun FilledHorizontalIconButton(
    text: String,
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
) {
    TextButtonContent(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerColor = containerColor,
        shape = shape,
        content = {
            Row(
                modifier = Modifier
                    .sizeIn(minHeight = 32.dp, minWidth = 140.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = when {
                        enabled -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    style = style,
                    color = when {
                        enabled -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                )
            }
        },
    )
}

@Composable
private fun TextButtonContent(
    onClick: () -> Unit,
    enabled: Boolean,
    containerColor: Color,
    shape: Shape,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = containerColor,
        ),
        shape = shape,
    ) {
        content()
    }
}

@Composable
fun HorizontalOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textPadding: Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.small,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.padding(2.dp),
        enabled = enabled,
        shape = shape,
        content = {
            if (leadingIcon != null) {
                Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) { leadingIcon() }
            }
            Box(
                Modifier.padding(
                    start = when {
                        leadingIcon != null -> ButtonDefaults.IconSpacing
                        else -> 0.dp
                    },
                ),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                    modifier = Modifier.padding(textPadding),
                )
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                enabled -> borderColor
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            },
        ),
    )
}

@Composable
fun OutlinedVerticalIconButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    leadingIcon: @Composable (() -> Unit) = {},
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.widthIn(min = 140.dp),
        enabled = enabled,
        shape = shape,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                leadingIcon()

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        enabled -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                enabled -> borderColor
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            },
        ),
    )
}

@Composable
fun OutlinedVerticalIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    text: @Composable (() -> Unit) = {},
    leadingIcon: @Composable (() -> Unit) = {},
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.widthIn(min = 140.dp),
        enabled = enabled,
        shape = shape,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                leadingIcon()

                text()
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                enabled -> borderColor
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            },
        ),
    )
}

@Composable
fun ScrimButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    show: Boolean = false,
    color: Color = MaterialTheme.colorScheme.surface,
    alpha: Float = 0.4f,
    content: @Composable () -> Unit,
) {
    val isLight = color.luminance() > 0.5
    val scrimEnabled = !show
    if (scrimEnabled) {
        val appTheme = if (isLight) AppTheme.LIGHT_THEME else AppTheme.DARK_THEME
        TvManiacTheme(appTheme = appTheme) {
            IconButton(
                onClick = onClick,
                modifier = modifier.iconButtonBackgroundScrim(enabled = true, alpha = alpha),
            ) {
                content()
            }
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier.iconButtonBackgroundScrim(enabled = false, alpha = alpha),
        ) {
            content()
        }
    }
}

@Composable
fun RefreshButton(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Crossfade(isRefreshing, label = "ActionButtonCrossfade") { targetRefreshing ->
        if (targetRefreshing) {
            AutoSizedCircularProgressIndicator(
                modifier = modifier,
            )
        } else {
            content()
        }
    }
}

@ThemePreviews
@Composable
private fun FilledTextButtonPreview() {
    TvManiacTheme {
        Surface {
            FilledTextButton(
                onClick = {},
                enabled = false,
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .background(color = MaterialTheme.colorScheme.secondary),
            ) {
                Text(
                    text = "Horror",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun FilledIconButtonPreview(@PreviewParameter(ButtonPreviewParamProvider::class) isEnable: Boolean) {
    TvManiacTheme {
        Surface {
            FilledVerticalIconButton(
                onClick = {},
                enabled = isEnable,
                text = "Track",
                imageVector = Icons.Default.LibraryAddCheck,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun FilledHorizontalIconButtonPreview(@PreviewParameter(ButtonPreviewParamProvider::class) isEnable: Boolean) {
    TvManiacTheme {
        Surface {
            FilledHorizontalIconButton(
                onClick = {},
                enabled = isEnable,
                text = "Add To Library",
                imageVector = Icons.Default.LibraryAddCheck,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun TvManiacAlphaTextButtonPreview() {
    TvManiacTheme {
        Surface {
            FilledTextButton(
                onClick = {},
                enabled = false,
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                ),
            ) {
                Text(
                    text = "Horror",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun TvManiacOutlinedButtonPreview() {
    TvManiacTheme {
        Surface {
            OutlinedVerticalIconButton(
                onClick = {},
                enabled = true,
                leadingIcon = {
                    Image(
                        imageVector = Icons.Filled.LibraryAddCheck,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.8F,
                            ),
                        ),
                    )
                },
                text = "Following",
            )
        }
    }
}

private class ButtonPreviewParamProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(
        true,
        false,
    )
}
