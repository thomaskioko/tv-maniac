package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun TvManiacTextButton(
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
fun TvManiacOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textPadding: Dp = 4.dp,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    TvManiacOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        borderColor = borderColor,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        TvManiacButtonContent(
            text = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(textPadding),
                )
            },
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
fun TvManiacOutlinedButton(
    onClick: () -> Unit,
    borderColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(2.dp),
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        content = content,
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
private fun TvManiacButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            leadingIcon()
        }
    }
    Box(
        Modifier
            .padding(
                start = when {
                    leadingIcon != null -> ButtonDefaults.IconSpacing
                    else -> 0.dp
                },
            ),
    ) {
        text()
    }
}

@ThemePreviews
@Composable
fun TvManiacTextButtonPreview() {
    TvManiacTheme {
        Surface {
            TvManiacTextButton(
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
fun TvManiacAlphaTextButtonPreview() {
    TvManiacTheme {
        Surface {
            TvManiacTextButton(
                onClick = {},
                enabled = false,
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                ),
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
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
fun TvManiacOutlinedButtonPreview() {
    TvManiacTheme {
        Surface {
            TvManiacOutlinedButton(
                onClick = {},
                enabled = true,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_check_box_24),
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
