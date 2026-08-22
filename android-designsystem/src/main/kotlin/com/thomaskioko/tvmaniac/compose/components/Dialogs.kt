package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

@Composable
public fun TvManiacAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    icon: ImageVector? = null,
    dismissButtonText: String? = null,
    confirmButtonTestTag: String? = null,
    dismissButtonTestTag: String? = null,
    neutralButtonText: String? = null,
    onNeutral: (() -> Unit)? = null,
    neutralButtonTestTag: String? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val density = LocalDensity.current
    val containerWidth = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = (containerWidth - 80.dp).coerceAtLeast(0.dp)),
        shape = shape,
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.medium)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                content?.invoke()
            }
        },
        confirmButton = {
            if (dismissButtonText != null && neutralButtonText != null && onNeutral != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
                ) {
                    DialogTextButton(
                        text = confirmButtonText,
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onConfirm,
                        testTag = confirmButtonTestTag,
                    )
                    DialogTextButton(
                        text = neutralButtonText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onNeutral,
                        testTag = neutralButtonTestTag,
                    )
                    DialogTextButton(
                        text = dismissButtonText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onDismiss,
                        testTag = dismissButtonTestTag,
                    )
                }
            } else {
                DialogTextButton(
                    text = confirmButtonText,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onConfirm,
                    testTag = confirmButtonTestTag,
                )
            }
        },
        dismissButton = if (dismissButtonText != null && neutralButtonText != null && onNeutral != null) {
            null
        } else {
            dismissButtonText?.let { dismissText ->
                {
                    DialogTextButton(
                        text = dismissText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onDismiss,
                        testTag = dismissButtonTestTag,
                    )
                }
            }
        },
    )
}

@Composable
private fun DialogTextButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    testTag: String?,
) {
    TextButton(
        modifier = testTag?.let { Modifier.testTag(it) } ?: Modifier,
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            textAlign = TextAlign.End,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacAlertDialogPreview() {
    TvManiacAlertDialog(
        title = "Enable Notifications",
        message = "Get notified when new episodes of your favorite shows are released.",
        confirmButtonText = "Enable",
        dismissButtonText = "Not Now",
        icon = Icons.Default.Info,
        onConfirm = {},
        onDismiss = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacAlertDialogNoIconPreview() {
    TvManiacAlertDialog(
        title = "Confirm Action",
        message = "Are you sure you want to proceed with this action?",
        confirmButtonText = "Confirm",
        dismissButtonText = "Cancel",
        onConfirm = {},
        onDismiss = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacAlertDialogThreeActionsPreview() {
    TvManiacAlertDialog(
        title = "Restore this backup?",
        message = "This replaces the shows and watch history on this device. " +
            "Add it to Trakt to keep it synced, or restore on this device only.",
        confirmButtonText = "Restore and sync with Trakt",
        dismissButtonText = "Cancel",
        neutralButtonText = "Restore locally",
        onConfirm = {},
        onDismiss = {},
        onNeutral = {},
    )
}
